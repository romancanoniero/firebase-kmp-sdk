package com.iyr.firebase.crashlytics

expect class FirebaseCrashlytics {
    companion object {
        fun getInstance(): FirebaseCrashlytics
    }
    
    fun recordException(throwable: Throwable)
    fun log(message: String)
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Boolean)
    fun setCustomKey(key: String, value: Int)
    fun setCustomKey(key: String, value: Long)
    fun setCustomKey(key: String, value: Float)
    fun setCustomKey(key: String, value: Double)
    fun setCustomKeys(keysAndValues: Map<String, Any>)
    fun setCrashlyticsCollectionEnabled(enabled: Boolean)
    suspend fun checkForUnsentReports(): Boolean
    fun sendUnsentReports()
    fun deleteUnsentReports()
    fun isCrashlyticsCollectionEnabled(): Boolean
}
