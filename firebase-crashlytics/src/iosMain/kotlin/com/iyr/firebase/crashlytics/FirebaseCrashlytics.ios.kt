package com.iyr.firebase.crashlytics

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import platform.Foundation.NSException
import platform.Foundation.NSError

actual class FirebaseCrashlytics internal constructor(
    private val ios: FIRCrashlytics
) {
    actual companion object {
        actual fun getInstance(): FirebaseCrashlytics = 
            FirebaseCrashlytics(FIRCrashlytics.crashlytics())
    }
    
    actual fun recordException(throwable: Throwable) {
        ios.recordError(
            NSError.errorWithDomain(
                "KotlinException",
                code = 0,
                userInfo = mapOf(
                    "message" to (throwable.message ?: "Unknown error"),
                    "stackTrace" to throwable.stackTraceToString()
                )
            )
        )
    }
    
    actual fun log(message: String) {
        ios.log(message)
    }
    
    actual fun setUserId(userId: String) {
        ios.setUserID(userId)
    }
    
    actual fun setCustomKey(key: String, value: String) {
        ios.setCustomValue(value, forKey = key)
    }
    
    actual fun setCustomKey(key: String, value: Boolean) {
        ios.setCustomValue(value, forKey = key)
    }
    
    actual fun setCustomKey(key: String, value: Int) {
        ios.setCustomValue(value, forKey = key)
    }
    
    actual fun setCustomKey(key: String, value: Long) {
        ios.setCustomValue(value, forKey = key)
    }
    
    actual fun setCustomKey(key: String, value: Float) {
        ios.setCustomValue(value, forKey = key)
    }
    
    actual fun setCustomKey(key: String, value: Double) {
        ios.setCustomValue(value, forKey = key)
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun setCustomKeys(keysAndValues: Map<String, Any>) {
        ios.setCustomKeysAndValues(keysAndValues as Map<Any?, *>)
    }
    
    actual fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        ios.setCrashlyticsCollectionEnabled(enabled)
    }
    
    actual suspend fun checkForUnsentReports(): Boolean {
        return ios.didCrashDuringPreviousExecution()
    }
    
    actual fun sendUnsentReports() {
        // iOS envía automáticamente
    }
    
    actual fun deleteUnsentReports() {
        ios.deleteUnsentReports()
    }
    
    actual fun isCrashlyticsCollectionEnabled(): Boolean {
        return ios.isCrashlyticsCollectionEnabled()
    }
}

