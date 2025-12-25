@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.analytics

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise

@JsModule("firebase/analytics")
@JsNonModule
private external object AnalyticsModule {
    fun getAnalytics(): dynamic
    fun getAnalytics(app: dynamic): dynamic
    fun logEvent(analytics: dynamic, eventName: String, eventParams: dynamic?)
    fun setUserProperties(analytics: dynamic, properties: dynamic)
    fun setUserId(analytics: dynamic, id: String?)
    fun setAnalyticsCollectionEnabled(analytics: dynamic, enabled: Boolean)
    fun setDefaultEventParameters(analytics: dynamic, parameters: dynamic?)
    fun getGoogleAnalyticsClientId(analytics: dynamic): Promise<dynamic>
}

actual class FirebaseAnalytics internal constructor(private val jsAnalytics: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseAnalytics = FirebaseAnalytics(AnalyticsModule.getAnalytics())
        actual fun getInstance(app: FirebaseApp): FirebaseAnalytics = FirebaseAnalytics(AnalyticsModule.getAnalytics(app.js))
    }
    
    actual fun logEvent(name: String, params: Map<String, Any?>?) {
        AnalyticsModule.logEvent(jsAnalytics, name, params?.toJsObject())
    }
    
    actual fun setUserId(userId: String?) {
        AnalyticsModule.setUserId(jsAnalytics, userId)
    }
    
    actual fun setUserProperty(name: String, value: String?) {
        val props = js("{}"); props[name] = value
        AnalyticsModule.setUserProperties(jsAnalytics, props)
    }
    
    actual fun setSessionTimeoutDuration(milliseconds: Long) {}
    
    actual fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        AnalyticsModule.setAnalyticsCollectionEnabled(jsAnalytics, enabled)
    }
    
    actual fun resetAnalyticsData() {}
    
    actual suspend fun getAppInstanceId(): String? = suspendCancellableCoroutine { cont ->
        AnalyticsModule.getGoogleAnalyticsClientId(jsAnalytics)
            .then({ id -> cont.resume(id as? String) }, { cont.resume(null) })
    }
    
    actual fun setCurrentScreen(screenName: String, screenClass: String?) {
        val params = js("{}")
        params.screen_name = screenName
        screenClass?.let { params.screen_class = it }
        AnalyticsModule.logEvent(jsAnalytics, "screen_view", params)
    }
    
    actual fun setConsent(consentSettings: Map<ConsentType, ConsentStatus>) {}
    
    actual fun setDefaultEventParameters(params: Map<String, Any?>?) {
        AnalyticsModule.setDefaultEventParameters(jsAnalytics, params?.toJsObject())
    }
}

private fun Map<String, Any?>.toJsObject(): dynamic {
    val obj = js("{}")
    this.forEach { (k, v) -> obj[k] = v }
    return obj
}

private fun <T> Promise<T>.then(onFulfilled: (T) -> Unit, onRejected: (Throwable) -> Unit): Promise<T> {
    asDynamic().then(onFulfilled, onRejected)
    return this
}
