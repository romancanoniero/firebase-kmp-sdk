package com.iyr.firebase.database

import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.DeserializationStrategy

/**
 * Extensiones para deserializar DataSnapshot a objetos tipados.
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

private val json = Json { 
    ignoreUnknownKeys = true 
    isLenient = true
    coerceInputValues = true
}

/**
 * Convierte el valor del snapshot a un objeto del tipo especificado.
 * Requiere que la clase tenga la anotación @Serializable.
 * 
 * @return El objeto deserializado o null si no existe o hay error
 */
inline fun <reified T> DataSnapshot.getValue(): T? {
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
fun <T> DataSnapshot.getValue(deserializer: DeserializationStrategy<T>): T? {
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
 * @return Lista de objetos deserializados
 */
inline fun <reified T> DataSnapshot.getValueList(): List<T> {
    return children.mapNotNull { child ->
        child.getValue<T>()
    }
}

/**
 * Convierte los hijos del snapshot a un Map con las keys y valores tipados.
 * 
 * @return Map de key -> objeto deserializado
 */
inline fun <reified T> DataSnapshot.getValueMap(): Map<String, T> {
    return children.mapNotNull { child ->
        val key = child.key ?: return@mapNotNull null
        val value = child.getValue<T>() ?: return@mapNotNull null
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

// ==================== HELPERS INTERNOS ====================

/**
 * Convierte un valor Any? a JsonElement para deserialización.
 */
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

