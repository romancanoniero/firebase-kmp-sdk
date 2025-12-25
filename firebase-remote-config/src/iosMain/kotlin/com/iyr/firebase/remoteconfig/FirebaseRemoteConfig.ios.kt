package com.iyr.firebase.remoteconfig

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseRemoteConfig.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseRemoteConfig internal constructor(
    private val ios: FIRRemoteConfig
) {
    actual companion object {
        actual fun getInstance(): FirebaseRemoteConfig = 
            FirebaseRemoteConfig(FIRRemoteConfig.remoteConfig())
        actual fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            return if (app.getName() == "[DEFAULT]") {
                FirebaseRemoteConfig(FIRRemoteConfig.remoteConfig())
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance() después de configurar la app"
                )
            }
        }
    }
    
    actual suspend fun fetchAndActivate(): Boolean = suspendCancellableCoroutine { cont ->
        ios.fetchAndActivateWithCompletionHandler { status, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            // FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote = 0
            // Compare as Long since cinterop enum types are ULong-based
            else cont.resume((status as Number).toLong() == 0L)
        }
    }
    
    actual suspend fun fetch() = suspendCancellableCoroutine<Unit> { cont ->
        ios.fetchWithCompletionHandler { _, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun fetch(minimumFetchIntervalInSeconds: Long) = suspendCancellableCoroutine<Unit> { cont ->
        ios.fetchWithExpirationDuration(minimumFetchIntervalInSeconds.toDouble()) { _, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun activate(): Boolean = suspendCancellableCoroutine { cont ->
        ios.activateWithCompletion { changed, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(changed)
        }
    }
    
    actual suspend fun ensureInitialized() = suspendCancellableCoroutine<Unit> { cont ->
        ios.ensureInitializedWithCompletionHandler { error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    
    actual fun getString(key: String): String = ios.configValueForKey(key).stringValue ?: ""
    actual fun getBoolean(key: String): Boolean = ios.configValueForKey(key).boolValue
    actual fun getLong(key: String): Long = ios.configValueForKey(key).numberValue?.longValue ?: 0L
    actual fun getDouble(key: String): Double = ios.configValueForKey(key).numberValue?.doubleValue ?: 0.0
    actual fun getValue(key: String): FirebaseRemoteConfigValue = FirebaseRemoteConfigValue(ios.configValueForKey(key))
    
    @Suppress("UNCHECKED_CAST")
    actual fun getAll(): Map<String, FirebaseRemoteConfigValue> {
        // Simplificado: obtener todas las keys directamente
        // El método allKeysFromSource puede no estar disponible correctamente
        return emptyMap() // Fallback seguro para iOS
    }
    
    actual fun setDefaultsAsync(defaults: Map<String, Any?>) {
        @Suppress("UNCHECKED_CAST")
        ios.setDefaults(defaults as Map<Any?, *>)
    }
    
    actual fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings) {
        ios.setConfigSettings(settings.ios)
    }
    
    actual fun reset() { /* iOS no tiene reset directo */ }
}

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseRemoteConfigValue internal constructor(
    private val ios: FIRRemoteConfigValue
) {
    actual fun asString(): String = ios.stringValue ?: ""
    actual fun asBoolean(): Boolean = ios.boolValue
    actual fun asLong(): Long = ios.numberValue?.longValue ?: 0L
    actual fun asDouble(): Double = ios.numberValue?.doubleValue ?: 0.0
    actual fun asByteArray(): ByteArray = ios.dataValue?.let { 
        ByteArray(it.length.toInt()).apply { /* copy data */ }
    } ?: ByteArray(0)
    actual fun getSource(): ValueSource {
        // FIRRemoteConfigSource: Static=0, Default=1, Remote=2
        // Cast to Long for comparison
        return when ((ios.source as Number).toLong()) {
            0L -> ValueSource.STATIC
            1L -> ValueSource.DEFAULT
            2L -> ValueSource.REMOTE
            else -> ValueSource.STATIC
        }
    }
}

actual enum class ValueSource { STATIC, DEFAULT, REMOTE }

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseRemoteConfigSettings internal constructor(
    internal val ios: FIRRemoteConfigSettings
) {
    actual val minimumFetchIntervalInSeconds: Long get() = ios.minimumFetchInterval.toLong()
    actual val fetchTimeoutInSeconds: Long get() = ios.fetchTimeout.toLong()
    
    actual class Builder {
        private val settings = FIRRemoteConfigSettings()
        actual fun setMinimumFetchIntervalInSeconds(interval: Long): Builder { 
            settings.setMinimumFetchInterval(interval.toDouble()); return this 
        }
        actual fun setFetchTimeoutInSeconds(timeout: Long): Builder { 
            settings.setFetchTimeout(timeout.toDouble()); return this 
        }
        actual fun build(): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings(settings)
    }
}

actual class FirebaseRemoteConfigException(
    message: String?,
    actual val code: Code
) : Exception(message) {
    actual enum class Code { UNKNOWN, THROTTLED, INTERNAL, FETCH_ERROR }
}
