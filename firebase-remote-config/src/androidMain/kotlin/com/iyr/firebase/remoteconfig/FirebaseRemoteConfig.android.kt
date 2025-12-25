package com.iyr.firebase.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig as AndroidRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings as AndroidSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue as AndroidValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException as AndroidException
import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.tasks.await

actual class FirebaseRemoteConfig internal constructor(
    private val android: AndroidRemoteConfig
) {
    actual companion object {
        actual fun getInstance(): FirebaseRemoteConfig = 
            FirebaseRemoteConfig(AndroidRemoteConfig.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseRemoteConfig = 
            FirebaseRemoteConfig(AndroidRemoteConfig.getInstance(app.android))
    }
    
    actual suspend fun fetchAndActivate(): Boolean = android.fetchAndActivate().await()
    actual suspend fun fetch() { android.fetch().await() }
    actual suspend fun fetch(minimumFetchIntervalInSeconds: Long) { android.fetch(minimumFetchIntervalInSeconds).await() }
    actual suspend fun activate(): Boolean = android.activate().await()
    actual suspend fun ensureInitialized() { android.ensureInitialized().await() }
    
    actual fun getString(key: String): String = android.getString(key)
    actual fun getBoolean(key: String): Boolean = android.getBoolean(key)
    actual fun getLong(key: String): Long = android.getLong(key)
    actual fun getDouble(key: String): Double = android.getDouble(key)
    actual fun getValue(key: String): FirebaseRemoteConfigValue = FirebaseRemoteConfigValue(android.getValue(key))
    actual fun getAll(): Map<String, FirebaseRemoteConfigValue> = android.all.mapValues { FirebaseRemoteConfigValue(it.value) }
    
    actual fun setDefaultsAsync(defaults: Map<String, Any?>) { android.setDefaultsAsync(defaults) }
    actual fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings) { android.setConfigSettingsAsync(settings.android) }
    actual fun reset() { android.reset() }
}

actual class FirebaseRemoteConfigValue internal constructor(
    private val android: AndroidValue
) {
    actual fun asString(): String = android.asString()
    actual fun asBoolean(): Boolean = android.asBoolean()
    actual fun asLong(): Long = android.asLong()
    actual fun asDouble(): Double = android.asDouble()
    actual fun asByteArray(): ByteArray = android.asByteArray()
    actual fun getSource(): ValueSource = when (android.source) {
        AndroidRemoteConfig.VALUE_SOURCE_STATIC -> ValueSource.STATIC
        AndroidRemoteConfig.VALUE_SOURCE_DEFAULT -> ValueSource.DEFAULT
        AndroidRemoteConfig.VALUE_SOURCE_REMOTE -> ValueSource.REMOTE
        else -> ValueSource.STATIC
    }
}

actual enum class ValueSource { STATIC, DEFAULT, REMOTE }

actual class FirebaseRemoteConfigSettings internal constructor(
    internal val android: AndroidSettings
) {
    actual val minimumFetchIntervalInSeconds: Long get() = android.minimumFetchIntervalInSeconds
    actual val fetchTimeoutInSeconds: Long get() = android.fetchTimeoutInSeconds
    
    actual class Builder {
        private val builder = AndroidSettings.Builder()
        actual fun setMinimumFetchIntervalInSeconds(interval: Long): Builder { builder.setMinimumFetchIntervalInSeconds(interval); return this }
        actual fun setFetchTimeoutInSeconds(timeout: Long): Builder { builder.setFetchTimeoutInSeconds(timeout); return this }
        actual fun build(): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings(builder.build())
    }
}

actual class FirebaseRemoteConfigException(
    message: String?,
    actual val code: Code
) : Exception(message) {
    actual enum class Code { UNKNOWN, THROTTLED, INTERNAL, FETCH_ERROR }
}

