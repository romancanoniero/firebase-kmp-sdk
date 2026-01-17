package com.iyr.firebase.core

import cocoapods.FirebaseCore.FIROptions

/**
 * FirebaseOptions - Implementación iOS
 * 
 * Wrapper sobre FIROptions del Firebase iOS SDK.
 */
actual class FirebaseOptions internal constructor(
    internal val ios: FIROptions
) {
    actual val apiKey: String get() = ios.APIKey ?: ""
    actual val applicationId: String get() = ios.googleAppID
    actual val databaseUrl: String? get() = ios.databaseURL
    actual val projectId: String? get() = ios.projectID
    actual val storageBucket: String? get() = ios.storageBucket
    actual val gcmSenderId: String? get() = ios.GCMSenderID
    actual val gaTrackingId: String? get() = ios.trackingID
    
    actual class Builder {
        private var _apiKey: String = ""
        private var _applicationId: String = ""
        private var _databaseUrl: String? = null
        private var _projectId: String? = null
        private var _storageBucket: String? = null
        private var _gcmSenderId: String? = null
        private var _gaTrackingId: String? = null
        
        actual constructor()
        
        actual constructor(options: FirebaseOptions) {
            _apiKey = options.apiKey
            _applicationId = options.applicationId
            _databaseUrl = options.databaseUrl
            _projectId = options.projectId
            _storageBucket = options.storageBucket
            _gcmSenderId = options.gcmSenderId
            _gaTrackingId = options.gaTrackingId
        }
        
        actual fun setApiKey(apiKey: String): Builder {
            _apiKey = apiKey
            return this
        }
        
        actual fun setApplicationId(applicationId: String): Builder {
            _applicationId = applicationId
            return this
        }
        
        actual fun setDatabaseUrl(databaseUrl: String?): Builder {
            _databaseUrl = databaseUrl
            return this
        }
        
        actual fun setProjectId(projectId: String?): Builder {
            _projectId = projectId
            return this
        }
        
        actual fun setStorageBucket(storageBucket: String?): Builder {
            _storageBucket = storageBucket
            return this
        }
        
        actual fun setGcmSenderId(gcmSenderId: String?): Builder {
            _gcmSenderId = gcmSenderId
            return this
        }
        
        actual fun setGaTrackingId(gaTrackingId: String?): Builder {
            _gaTrackingId = gaTrackingId
            return this
        }
        
        actual fun build(): FirebaseOptions {
            require(_apiKey.isNotEmpty()) { "API Key is required" }
            require(_applicationId.isNotEmpty()) { "Application ID is required" }
            
            val firOptions = FIROptions(
                googleAppID = _applicationId,
                GCMSenderID = _gcmSenderId ?: ""
            )
            firOptions.APIKey = _apiKey
            _databaseUrl?.let { firOptions.databaseURL = it }
            _projectId?.let { firOptions.projectID = it }
            _storageBucket?.let { firOptions.storageBucket = it }
            _gaTrackingId?.let { firOptions.trackingID = it }
            
            return FirebaseOptions(firOptions)
        }
    }
}






