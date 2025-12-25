package com.iyr.firebase.database

import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

/**
 * Extensiones para serializar/deserializar con Realtime Database.
 * Incluye funciones tipadas para lectura y escritura.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = snapshot.getValue<User>()
 * val users = snapshot.getValueList<User>()
 * ```
 */

@PublishedApi
internal val json = Json { 
    ignoreUnknownKeys = true 
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
}

/**
 * Convierte el valor del snapshot a un objeto del tipo especificado.
 * Requiere que la clase tenga la anotación @Serializable.
 * 
 * Uso:
 * ```
 * val user = snapshot.value<User>()
 * ```
 * 
 * @return El objeto deserializado o null si no existe o hay error
 */
inline fun <reified T> DataSnapshot.value(): T? {
    val rawValue = getValue() ?: return null
    return try {
        val jsonElement = rawValue.toJsonElement()
        json.decodeFromJsonElement(jsonElement)
    } catch (e: Exception) {
        null
    }
}

/**
 * Convierte el valor del snapshot a un objeto usando un deserializador específico.
 * 
 * @param deserializer El deserializador a usar
 * @return El objeto deserializado o null si no existe o hay error
 */
fun <T> DataSnapshot.value(deserializer: DeserializationStrategy<T>): T? {
    val rawValue = getValue() ?: return null
    return try {
        val jsonElement = rawValue.toJsonElement()
        json.decodeFromJsonElement(deserializer, jsonElement)
    } catch (e: Exception) {
        null
    }
}

/**
 * Convierte los hijos del snapshot a una lista de objetos del tipo especificado.
 * Útil para nodos que contienen múltiples items.
 * 
 * Uso:
 * ```
 * val users = snapshot.valueList<User>()
 * ```
 * 
 * @return Lista de objetos deserializados
 */
inline fun <reified T> DataSnapshot.valueList(): List<T> {
    return children.mapNotNull { child ->
        child.value<T>()
    }
}

/**
 * Convierte los hijos del snapshot a un Map con las keys y valores tipados.
 * 
 * Uso:
 * ```
 * val usersMap = snapshot.valueMap<User>()
 * ```
 * 
 * @return Map de key -> objeto deserializado
 */
inline fun <reified T> DataSnapshot.valueMap(): Map<String, T> {
    return children.mapNotNull { child ->
        val key = child.key ?: return@mapNotNull null
        val value = child.value<T>() ?: return@mapNotNull null
        key to value
    }.toMap()
}

/**
 * Obtiene un campo específico como String.
 */
fun DataSnapshot.getString(field: String): String? {
    return child(field).getValue() as? String
}

/**
 * Obtiene un campo específico como Long.
 */
fun DataSnapshot.getLong(field: String): Long? {
    val value = child(field).getValue()
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
fun DataSnapshot.getInt(field: String): Int? {
    return getLong(field)?.toInt()
}

/**
 * Obtiene un campo específico como Double.
 */
fun DataSnapshot.getDouble(field: String): Double? {
    val value = child(field).getValue()
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
fun DataSnapshot.getBoolean(field: String): Boolean? {
    val value = child(field).getValue()
    return when (value) {
        is Boolean -> value
        is String -> value.lowercase() == "true"
        is Number -> value.toInt() != 0
        else -> null
    }
}

/**
 * Obtiene un campo específico como lista de Strings.
 */
@Suppress("UNCHECKED_CAST")
fun DataSnapshot.getStringList(field: String): List<String>? {
    val value = child(field).getValue()
    return (value as? List<*>)?.filterIsInstance<String>()
}

// ==================== EXTENSIONES PARA ESCRIBIR (DatabaseReference) ====================

/**
 * Guarda un objeto @Serializable en la referencia.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = User("John", 30)
 * database.getReference("users/user1").set(user)
 * ```
 */
suspend inline fun <reified T> DatabaseReference.set(value: T) {
    val map = value.toFirebaseMap()
    setValue(map as Any?)
}

/**
 * Guarda un objeto usando un serializador específico.
 */
suspend fun <T> DatabaseReference.set(value: T, serializer: SerializationStrategy<T>) {
    val map = value.toFirebaseMap(serializer)
    setValue(map as Any?)
}

/**
 * Actualiza campos usando un objeto @Serializable.
 * Solo actualiza los campos presentes en el objeto.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class UserUpdate(val name: String? = null, val age: Int? = null)
 * 
 * database.getReference("users/user1").update(UserUpdate(name = "Jane"))
 * ```
 */
suspend inline fun <reified T> DatabaseReference.update(value: T) {
    val map = value.toFirebaseMap()
    updateChildren(map)
}

/**
 * Push y guarda un objeto @Serializable, retorna la referencia creada.
 * 
 * Uso:
 * ```
 * val post = Post(title = "Hello", content = "World")
 * val newRef = database.getReference("posts").pushValue(post)
 * println("Created: ${newRef.key}")
 * ```
 */
suspend inline fun <reified T> DatabaseReference.pushValue(value: T): DatabaseReference {
    val newRef = push()
    newRef.set(value)
    return newRef
}

// ==================== SERIALIZACIÓN DE OBJETOS ====================

/**
 * Convierte un objeto @Serializable a Map para guardar en Firebase.
 * 
 * Uso:
 * ```
 * @Serializable
 * data class User(val name: String, val age: Int)
 * 
 * val user = User("John", 30)
 * val map = user.toFirebaseMap()
 * // map = {"name": "John", "age": 30}
 * ```
 */
inline fun <reified T> T.toFirebaseMap(): Map<String, Any?> {
    val jsonElement = json.encodeToJsonElement(this)
    return jsonElement.toFirebaseValue() as? Map<String, Any?> ?: emptyMap()
}

/**
 * Convierte un objeto usando un serializador específico.
 */
fun <T> T.toFirebaseMap(serializer: SerializationStrategy<T>): Map<String, Any?> {
    val jsonElement = json.encodeToJsonElement(serializer, this)
    return jsonElement.toFirebaseValue() as? Map<String, Any?> ?: emptyMap()
}

/**
 * Convierte un objeto @Serializable a Any para setValue.
 * Puede retornar Map, List, o valor primitivo según el tipo.
 */
inline fun <reified T> T.toFirebaseValue(): Any? {
    val jsonElement = json.encodeToJsonElement(this)
    return jsonElement.toFirebaseValue()
}

// ==================== HELPERS INTERNOS ====================

/**
 * Convierte un valor Any? a JsonElement para deserialización.
 */
@PublishedApi
@Suppress("UNCHECKED_CAST")
internal fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Map<*, *> -> {
        val map = this as Map<String, Any?>
        JsonObject(map.mapValues { (_, v) -> v.toJsonElement() })
    }
    is List<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> JsonPrimitive(this.toString())
}

/**
 * Convierte JsonElement a valor Firebase (Map, List, o primitivo).
 */
@PublishedApi
internal fun JsonElement.toFirebaseValue(): Any? = when (this) {
    is JsonNull -> null
    is JsonPrimitive -> {
        when {
            isString -> content
            content == "true" -> true
            content == "false" -> false
            content.contains('.') -> content.toDoubleOrNull() ?: content
            else -> content.toLongOrNull() ?: content
        }
    }
    is JsonObject -> this.mapValues { (_, v) -> v.toFirebaseValue() }
    is JsonArray -> this.map { it.toFirebaseValue() }
}

