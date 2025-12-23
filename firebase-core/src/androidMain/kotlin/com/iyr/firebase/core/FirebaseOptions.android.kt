package com.iyr.firebase.core

/**
 * FirebaseOptions - Implementación Android
 * 
 * Wrapper sobre com.google.firebase.FirebaseOptions
 */
actual class FirebaseOptions internal constructor(
    internal val android: com.google.firebase.FirebaseOptions
) {
    actual val apiKey: String get() = android.apiKey
    actual val applicationId: String get() = android.applicationId
    actual val databaseUrl: String? get() = android.databaseUrl
    actual val projectId: String? get() = android.projectId
    actual val storageBucket: String? get() = android.storageBucket
    actual val gcmSenderId: String? get() = android.gcmSenderId
    actual val gaTrackingId: String? get() = android.gaTrackingId
    
    actual class Builder {
        private val androidBuilder = com.google.firebase.FirebaseOptions.Builder()
        
        actual constructor()
        
        actual constructor(options: FirebaseOptions) {
            androidBuilder.setApiKey(options.apiKey)
            androidBuilder.setApplicationId(options.applicationId)
            options.databaseUrl?.let { androidBuilder.setDatabaseUrl(it) }
            options.projectId?.let { androidBuilder.setProjectId(it) }
            options.storageBucket?.let { androidBuilder.setStorageBucket(it) }
            options.gcmSenderId?.let { androidBuilder.setGcmSenderId(it) }
            options.gaTrackingId?.let { androidBuilder.setGaTrackingId(it) }
        }
        
        actual fun setApiKey(apiKey: String): Builder {
            androidBuilder.setApiKey(apiKey)
            return this
        }
        
        actual fun setApplicationId(applicationId: String): Builder {
            androidBuilder.setApplicationId(applicationId)
            return this
        }
        
        actual fun setDatabaseUrl(databaseUrl: String?): Builder {
            databaseUrl?.let { androidBuilder.setDatabaseUrl(it) }
            return this
        }
        
        actual fun setProjectId(projectId: String?): Builder {
            projectId?.let { androidBuilder.setProjectId(it) }
            return this
        }
        
        actual fun setStorageBucket(storageBucket: String?): Builder {
            storageBucket?.let { androidBuilder.setStorageBucket(it) }
            return this
        }
        
        actual fun setGcmSenderId(gcmSenderId: String?): Builder {
            gcmSenderId?.let { androidBuilder.setGcmSenderId(it) }
            return this
        }
        
        actual fun setGaTrackingId(gaTrackingId: String?): Builder {
            gaTrackingId?.let { androidBuilder.setGaTrackingId(it) }
            return this
        }
        
        actual fun build(): FirebaseOptions {
            return FirebaseOptions(androidBuilder.build())
        }
    }
}

