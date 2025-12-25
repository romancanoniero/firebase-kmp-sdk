package com.iyr.firebase.firestore

import kotlinx.serialization.json.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

/**
 * Extensiones para deserializar DocumentSnapshot y QuerySnapshot a objetos tipados.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = documentSnapshot.toObject<User>()
 * val users = querySnapshot.toObjects<User>()
 * ```
 */

private val json = Json { 
    ignoreUnknownKeys = true 
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
}

// ==================== DOCUMENT SNAPSHOT ====================

/**
 * Convierte el documento a un objeto del tipo especificado.
 * Requiere que la clase tenga la anotación @Serializable.
 * 
 * @return El objeto deserializado o null si no existe o hay error
 */
inline fun <reified T> DocumentSnapshot.toObject(): T? {
    val data = getData() ?: return null
    return try {
        val jsonElement = data.toJsonElement()
        json.decodeFromJsonElement(jsonElement)
    } catch (e: Exception) {
        null
    }
}

/**
 * Convierte el documento a un objeto usando un deserializador específico.
 * 
 * @param deserializer El deserializador a usar
 * @return El objeto deserializado o null si no existe o hay error
 */
fun <T> DocumentSnapshot.toObject(deserializer: DeserializationStrategy<T>): T? {
    val data = getData() ?: return null
    return try {
        val jsonElement = data.toJsonElement()
        json.decodeFromJsonElement(deserializer, jsonElement)
    } catch (e: Exception) {
        null
    }
}

/**
 * Obtiene un campo específico como String.
 */
fun DocumentSnapshot.getString(field: String): String? {
    return get(field) as? String
}

/**
 * Obtiene un campo específico como Long.
 */
fun DocumentSnapshot.getLong(field: String): Long? {
    val value = get(field)
    return when (value) {
        is Long -> value
        is Int -> value.toLong()
        is Double -> value.toLong()
        is String -> value.toLongOrNull()
        else -> null
    }
}

/**
 * Obtiene un campo específico como Int.
 */
fun DocumentSnapshot.getInt(field: String): Int? {
    return getLong(field)?.toInt()
}

/**
 * Obtiene un campo específico como Double.
 */
fun DocumentSnapshot.getDouble(field: String): Double? {
    val value = get(field)
    return when (value) {
        is Double -> value
        is Float -> value.toDouble()
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }
}

/**
 * Obtiene un campo específico como Boolean.
 */
fun DocumentSnapshot.getBoolean(field: String): Boolean? {
    val value = get(field)
    return when (value) {
        is Boolean -> value
        is String -> value.lowercase() == "true"
        is Number -> value.toInt() != 0
        else -> null
    }
}

/**
 * Obtiene un campo específico como Timestamp (Long milliseconds).
 */
fun DocumentSnapshot.getTimestamp(field: String): Long? {
    val value = get(field)
    return when (value) {
        is Long -> value
        is Double -> value.toLong()
        is Map<*, *> -> {
            // Firestore Timestamp format: { seconds: Long, nanoseconds: Int }
            val seconds = (value["seconds"] as? Number)?.toLong() ?: return null
            val nanos = (value["nanoseconds"] as? Number)?.toInt() ?: 0
            seconds * 1000 + nanos / 1_000_000
        }
        else -> null
    }
}

/**
 * Obtiene un campo específico como lista de Strings.
 */
@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.getStringList(field: String): List<String>? {
    val value = get(field)
    return (value as? List<*>)?.filterIsInstance<String>()
}

/**
 * Obtiene un campo específico como Map.
 */
@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.getMap(field: String): Map<String, Any?>? {
    return get(field) as? Map<String, Any?>
}

// ==================== QUERY SNAPSHOT ====================

/**
 * Convierte todos los documentos a una lista de objetos del tipo especificado.
 * 
 * @return Lista de objetos deserializados (excluye nulls)
 */
inline fun <reified T> QuerySnapshot.toObjects(): List<T> {
    return documents.mapNotNull { it.toObject<T>() }
}

/**
 * Convierte todos los documentos usando un deserializador específico.
 * 
 * @param deserializer El deserializador a usar
 * @return Lista de objetos deserializados (excluye nulls)
 */
fun <T> QuerySnapshot.toObjects(deserializer: DeserializationStrategy<T>): List<T> {
    return documents.mapNotNull { it.toObject(deserializer) }
}

/**
 * Convierte todos los documentos a un Map de ID -> Objeto.
 * 
 * @return Map de documentId -> objeto deserializado
 */
inline fun <reified T> QuerySnapshot.toObjectsMap(): Map<String, T> {
    return documents.mapNotNull { doc ->
        val obj = doc.toObject<T>() ?: return@mapNotNull null
        doc.id to obj
    }.toMap()
}

// ==================== SERIALIZATION HELPERS ====================

/**
 * Convierte un objeto @Serializable a Map para guardar en Firestore.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = User("John", 30)
 * val data = user.toFirestoreMap()
 * documentRef.set(data)
 * ```
 */
inline fun <reified T> T.toFirestoreMap(): Map<String, Any?> {
    val jsonElement = json.encodeToJsonElement(this)
    return jsonElement.toMap()
}

/**
 * Convierte un objeto usando un serializador específico.
 */
fun <T> T.toFirestoreMap(serializer: SerializationStrategy<T>): Map<String, Any?> {
    val jsonElement = json.encodeToJsonElement(serializer, this)
    return jsonElement.toMap()
}

// ==================== HELPERS INTERNOS ====================

/**
 * Convierte un Map a JsonElement para deserialización.
 */
@Suppress("UNCHECKED_CAST")
internal fun Map<String, Any?>.toJsonElement(): JsonElement {
    return JsonObject(this.mapValues { (_, v) -> v.toJsonElementInternal() })
}

@Suppress("UNCHECKED_CAST")
private fun Any?.toJsonElementInternal(): JsonElement = when (this) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Map<*, *> -> {
        val map = this as Map<String, Any?>
        JsonObject(map.mapValues { (_, v) -> v.toJsonElementInternal() })
    }
    is List<*> -> JsonArray(this.map { it.toJsonElementInternal() })
    is Array<*> -> JsonArray(this.map { it.toJsonElementInternal() })
    else -> JsonPrimitive(this.toString())
}

/**
 * Convierte JsonElement a Map para Firestore.
 */
@Suppress("UNCHECKED_CAST")
internal fun JsonElement.toMap(): Map<String, Any?> {
    return when (this) {
        is JsonObject -> this.mapValues { (_, v) -> v.toAny() }
        else -> emptyMap()
    }
}

private fun JsonElement.toAny(): Any? = when (this) {
    is JsonNull -> null
    is JsonPrimitive -> {
        when {
            isString -> content
            content == "true" || content == "false" -> content.toBoolean()
            content.contains('.') -> content.toDoubleOrNull() ?: content
            else -> content.toLongOrNull() ?: content
        }
    }
    is JsonObject -> this.mapValues { (_, v) -> v.toAny() }
    is JsonArray -> this.map { it.toAny() }
}

