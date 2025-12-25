package com.iyr.firebase.remoteconfig

import com.iyr.firebase.core.FirebaseApp

expect class FirebaseRemoteConfig {
    companion object {
        fun getInstance(): FirebaseRemoteConfig
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig
    }
    
    suspend fun fetchAndActivate(): Boolean
    suspend fun fetch(): Unit
    suspend fun fetch(minimumFetchIntervalInSeconds: Long): Unit
    suspend fun activate(): Boolean
    suspend fun ensureInitialized(): Unit
    
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getLong(key: String): Long
    fun getDouble(key: String): Double
    fun getValue(key: String): FirebaseRemoteConfigValue
    fun getAll(): Map<String, FirebaseRemoteConfigValue>
    
    fun setDefaultsAsync(defaults: Map<String, Any?>)
    fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings)
    fun reset()
}

expect class FirebaseRemoteConfigValue {
    fun asString(): String
    fun asBoolean(): Boolean
    fun asLong(): Long
    fun asDouble(): Double
    fun asByteArray(): ByteArray
    fun getSource(): ValueSource
}

expect enum class ValueSource {
    STATIC,
    DEFAULT,
    REMOTE
}

expect class FirebaseRemoteConfigSettings {
    val minimumFetchIntervalInSeconds: Long
    val fetchTimeoutInSeconds: Long
    
    class Builder {
        fun setMinimumFetchIntervalInSeconds(interval: Long): Builder
        fun setFetchTimeoutInSeconds(timeout: Long): Builder
        fun build(): FirebaseRemoteConfigSettings
    }
}

expect class FirebaseRemoteConfigException : Exception {
    val code: Code
    
    enum class Code {
        UNKNOWN,
        THROTTLED,
        INTERNAL,
        FETCH_ERROR
    }
}

