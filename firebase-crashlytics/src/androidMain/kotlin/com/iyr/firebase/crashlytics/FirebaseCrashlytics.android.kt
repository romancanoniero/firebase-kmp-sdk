package com.iyr.firebase.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics as AndroidCrashlytics
import kotlinx.coroutines.tasks.await

actual class FirebaseCrashlytics internal constructor(
    private val android: AndroidCrashlytics
) {
    actual companion object {
        actual fun getInstance(): FirebaseCrashlytics = 
            FirebaseCrashlytics(AndroidCrashlytics.getInstance())
    }
    
    actual fun recordException(throwable: Throwable) {
        android.recordException(throwable)
    }
    
    actual fun log(message: String) {
        android.log(message)
    }
    
    actual fun setUserId(userId: String) {
        android.setUserId(userId)
    }
    
    actual fun setCustomKey(key: String, value: String) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKey(key: String, value: Boolean) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKey(key: String, value: Int) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKey(key: String, value: Long) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKey(key: String, value: Float) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKey(key: String, value: Double) {
        android.setCustomKey(key, value)
    }
    
    actual fun setCustomKeys(keysAndValues: Map<String, Any>) {
        keysAndValues.forEach { (key, value) ->
            when (value) {
                is String -> android.setCustomKey(key, value)
                is Boolean -> android.setCustomKey(key, value)
                is Int -> android.setCustomKey(key, value)
                is Long -> android.setCustomKey(key, value)
                is Float -> android.setCustomKey(key, value)
                is Double -> android.setCustomKey(key, value)
            }
        }
    }
    
    actual fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        android.setCrashlyticsCollectionEnabled(enabled)
    }
    
    actual suspend fun checkForUnsentReports(): Boolean {
        return android.checkForUnsentReports().await()
    }
    
    actual fun sendUnsentReports() {
        android.sendUnsentReports()
    }
    
    actual fun deleteUnsentReports() {
        android.deleteUnsentReports()
    }
    
    actual fun isCrashlyticsCollectionEnabled(): Boolean {
        return android.isCrashlyticsCollectionEnabled
    }
}

