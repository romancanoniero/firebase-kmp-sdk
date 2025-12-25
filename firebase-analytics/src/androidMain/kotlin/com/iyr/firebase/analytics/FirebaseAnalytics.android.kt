package com.iyr.firebase.analytics

import android.os.Bundle
import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics as AndroidAnalytics
import kotlinx.coroutines.tasks.await

actual class FirebaseAnalytics internal constructor(
    private val android: AndroidAnalytics
) {
    actual companion object {
        actual fun getInstance(): FirebaseAnalytics {
            val context = com.google.firebase.FirebaseApp.getInstance().applicationContext
            return FirebaseAnalytics(AndroidAnalytics.getInstance(context))
        }
        
        actual fun getInstance(app: FirebaseApp): FirebaseAnalytics {
            return FirebaseAnalytics(AndroidAnalytics.getInstance(app.android.applicationContext))
        }
    }
    
    actual fun logEvent(name: String, params: Map<String, Any?>?) {
        val bundle = params?.toBundle()
        android.logEvent(name, bundle)
    }
    
    actual fun setUserId(userId: String?) {
        android.setUserId(userId)
    }
    
    actual fun setUserProperty(name: String, value: String?) {
        android.setUserProperty(name, value)
    }
    
    actual fun setSessionTimeoutDuration(milliseconds: Long) {
        android.setSessionTimeoutDuration(milliseconds)
    }
    
    actual fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        android.setAnalyticsCollectionEnabled(enabled)
    }
    
    actual fun resetAnalyticsData() {
        android.resetAnalyticsData()
    }
    
    actual suspend fun getAppInstanceId(): String? {
        return android.appInstanceId.await()
    }
    
    @Suppress("DEPRECATION")
    actual fun setCurrentScreen(screenName: String, screenClass: String?) {
        // setCurrentScreen está deprecado, usar logEvent con screen_view
        val params = Bundle().apply {
            putString(AndroidAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(AndroidAnalytics.Param.SCREEN_CLASS, it) }
        }
        android.logEvent(AndroidAnalytics.Event.SCREEN_VIEW, params)
    }
    
    actual fun setConsent(consentSettings: Map<ConsentType, ConsentStatus>) {
        val androidConsent = consentSettings.mapKeys { (key, _) ->
            when (key) {
                ConsentType.AD_STORAGE -> AndroidAnalytics.ConsentType.AD_STORAGE
                ConsentType.AD_USER_DATA -> AndroidAnalytics.ConsentType.AD_USER_DATA
                ConsentType.AD_PERSONALIZATION -> AndroidAnalytics.ConsentType.AD_PERSONALIZATION
                ConsentType.ANALYTICS_STORAGE -> AndroidAnalytics.ConsentType.ANALYTICS_STORAGE
            }
        }.mapValues { (_, value) ->
            when (value) {
                ConsentStatus.GRANTED -> AndroidAnalytics.ConsentStatus.GRANTED
                ConsentStatus.DENIED -> AndroidAnalytics.ConsentStatus.DENIED
            }
        }
        android.setConsent(androidConsent)
    }
    
    actual fun setDefaultEventParameters(params: Map<String, Any?>?) {
        android.setDefaultEventParameters(params?.toBundle())
    }
    
    private fun Map<String, Any?>.toBundle(): Bundle = Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                null -> { /* skip */ }
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is Bundle -> putBundle(key, value)
                is Array<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    if (value.isArrayOf<Bundle>()) {
                        putParcelableArray(key, value as Array<Bundle>)
                    }
                }
                else -> putString(key, value.toString())
            }
        }
    }
}

