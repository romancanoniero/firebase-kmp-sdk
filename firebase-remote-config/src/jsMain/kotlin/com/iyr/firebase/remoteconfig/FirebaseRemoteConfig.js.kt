@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.remoteconfig

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise

@JsModule("firebase/remote-config")
@JsNonModule
private external object RemoteConfigModule {
    fun getRemoteConfig(): dynamic
    fun getRemoteConfig(app: dynamic): dynamic
    fun fetchAndActivate(rc: dynamic): Promise<dynamic>
    fun fetchConfig(rc: dynamic): Promise<dynamic>
    fun activate(rc: dynamic): Promise<dynamic>
    fun ensureInitialized(rc: dynamic): Promise<dynamic>
    fun getValue(rc: dynamic, key: String): dynamic
    fun getAll(rc: dynamic): dynamic
    fun getString(rc: dynamic, key: String): String
    fun getBoolean(rc: dynamic, key: String): Boolean
    fun getNumber(rc: dynamic, key: String): Double
}

actual class FirebaseRemoteConfig internal constructor(private val jsRc: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseRemoteConfig = FirebaseRemoteConfig(RemoteConfigModule.getRemoteConfig())
        actual fun getInstance(app: FirebaseApp): FirebaseRemoteConfig = FirebaseRemoteConfig(RemoteConfigModule.getRemoteConfig(app.js))
    }
    
    actual suspend fun fetchAndActivate(): Boolean = RemoteConfigModule.fetchAndActivate(jsRc).await() as Boolean
    actual suspend fun fetch() { RemoteConfigModule.fetchConfig(jsRc).await() }
    actual suspend fun fetch(minimumFetchIntervalInSeconds: Long) {
        jsRc.settings.minimumFetchIntervalMillis = minimumFetchIntervalInSeconds * 1000
        RemoteConfigModule.fetchConfig(jsRc).await()
    }
    actual suspend fun activate(): Boolean = RemoteConfigModule.activate(jsRc).await() as Boolean
    actual suspend fun ensureInitialized() { RemoteConfigModule.ensureInitialized(jsRc).await() }
    
    actual fun getString(key: String): String = RemoteConfigModule.getString(jsRc, key)
    actual fun getBoolean(key: String): Boolean = RemoteConfigModule.getBoolean(jsRc, key)
    actual fun getLong(key: String): Long = RemoteConfigModule.getNumber(jsRc, key).toLong()
    actual fun getDouble(key: String): Double = RemoteConfigModule.getNumber(jsRc, key)
    actual fun getValue(key: String): FirebaseRemoteConfigValue = FirebaseRemoteConfigValue(RemoteConfigModule.getValue(jsRc, key))
    
    actual fun getAll(): Map<String, FirebaseRemoteConfigValue> {
        val jsAll = RemoteConfigModule.getAll(jsRc)
        val keys = js("Object.keys(jsAll)") as Array<String>
        return keys.associateWith { k -> FirebaseRemoteConfigValue(jsAll[k]) }
    }
    
    actual fun setDefaultsAsync(defaults: Map<String, Any?>) {
        val jsDefaults = js("{}")
        defaults.forEach { (k, v) -> jsDefaults[k] = v }
        jsRc.defaultConfig = jsDefaults
    }
    
    actual fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings) {
        jsRc.settings.minimumFetchIntervalMillis = settings.minimumFetchIntervalInSeconds * 1000
        jsRc.settings.fetchTimeoutMillis = settings.fetchTimeoutInSeconds * 1000
    }
    
    actual fun reset() {}
}

actual class FirebaseRemoteConfigValue internal constructor(private val jsValue: dynamic) {
    actual fun asString(): String = jsValue.asString() as String
    actual fun asBoolean(): Boolean = jsValue.asBoolean() as Boolean
    actual fun asLong(): Long = (jsValue.asNumber() as Number).toLong()
    actual fun asDouble(): Double = jsValue.asNumber() as Double
    actual fun asByteArray(): ByteArray = ByteArray(0)
    actual fun getSource(): ValueSource = when (jsValue.getSource() as String) {
        "static" -> ValueSource.STATIC
        "default" -> ValueSource.DEFAULT
        "remote" -> ValueSource.REMOTE
        else -> ValueSource.STATIC
    }
}

actual enum class ValueSource { STATIC, DEFAULT, REMOTE }

actual class FirebaseRemoteConfigSettings internal constructor(
    actual val minimumFetchIntervalInSeconds: Long = 43200,
    actual val fetchTimeoutInSeconds: Long = 60
) {
    actual class Builder {
        private var minInterval = 43200L
        private var timeout = 60L
        actual fun setMinimumFetchIntervalInSeconds(interval: Long): Builder { minInterval = interval; return this }
        actual fun setFetchTimeoutInSeconds(timeout: Long): Builder { this.timeout = timeout; return this }
        actual fun build(): FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings(minInterval, timeout)
    }
}

actual class FirebaseRemoteConfigException internal constructor(message: String, actual val code: Code) : Exception(message) {
    actual enum class Code { UNKNOWN, THROTTLED, INTERNAL, FETCH_ERROR }
}

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}

private fun <T> Promise<T>.then(onFulfilled: (T) -> Unit, onRejected: (Throwable) -> Unit): Promise<T> {
    asDynamic().then(onFulfilled, onRejected)
    return this
}
