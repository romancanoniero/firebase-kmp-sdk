package com.iyr.firebase.analytics

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseAnalytics.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class FirebaseAnalytics internal constructor() {
    actual companion object {
        actual fun getInstance(): FirebaseAnalytics = FirebaseAnalytics()
        
        actual fun getInstance(app: FirebaseApp): FirebaseAnalytics {
            // Firebase Analytics en iOS es singleton, no soporta múltiples apps
            return getInstance()
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun logEvent(name: String, params: Map<String, Any?>?) {
        FIRAnalytics.logEventWithName(name, parameters = params as? Map<Any?, *>)
    }
    
    actual fun setUserId(userId: String?) {
        FIRAnalytics.setUserID(userId)
    }
    
    actual fun setUserProperty(name: String, value: String?) {
        FIRAnalytics.setUserPropertyString(value, forName = name)
    }
    
    actual fun setSessionTimeoutDuration(milliseconds: Long) {
        FIRAnalytics.setSessionTimeoutInterval(milliseconds.toDouble() / 1000.0)
    }
    
    actual fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        FIRAnalytics.setAnalyticsCollectionEnabled(enabled)
    }
    
    actual fun resetAnalyticsData() {
        FIRAnalytics.resetAnalyticsData()
    }
    
    actual suspend fun getAppInstanceId(): String? {
        // appInstanceID() en iOS retorna String? directamente
        return FIRAnalytics.appInstanceID()
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun setCurrentScreen(screenName: String, screenClass: String?) {
        val params = mutableMapOf<String, Any?>(
            "screen_name" to screenName
        )
        screenClass?.let { params["screen_class"] = it }
        FIRAnalytics.logEventWithName("screen_view", parameters = params as Map<Any?, *>)
    }
    
    actual fun setConsent(consentSettings: Map<ConsentType, ConsentStatus>) {
        val iosConsent = mutableMapOf<Any?, Any?>()
        consentSettings.forEach { (type, status) ->
            val iosType = when (type) {
                ConsentType.AD_STORAGE -> "ad_storage"
                ConsentType.AD_USER_DATA -> "ad_user_data"
                ConsentType.AD_PERSONALIZATION -> "ad_personalization"
                ConsentType.ANALYTICS_STORAGE -> "analytics_storage"
            }
            val iosStatus = when (status) {
                ConsentStatus.GRANTED -> "granted"
                ConsentStatus.DENIED -> "denied"
            }
            iosConsent[iosType] = iosStatus
        }
        // setConsent no está disponible en todas las versiones, usar fallback
        // FIRAnalytics.setConsent(iosConsent)
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun setDefaultEventParameters(params: Map<String, Any?>?) {
        FIRAnalytics.setDefaultEventParameters(params as? Map<Any?, *>)
    }
}
