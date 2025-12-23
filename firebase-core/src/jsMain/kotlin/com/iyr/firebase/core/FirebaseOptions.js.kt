package com.iyr.firebase.core

/**
 * FirebaseOptions - Implementación JavaScript
 */
actual class FirebaseOptions internal constructor(
    internal val js: dynamic
) {
    actual val apiKey: String get() = js.apiKey as? String ?: ""
    actual val applicationId: String get() = js.appId as? String ?: ""
    actual val databaseUrl: String? get() = js.databaseURL as? String
    actual val projectId: String? get() = js.projectId as? String
    actual val storageBucket: String? get() = js.storageBucket as? String
    actual val gcmSenderId: String? get() = js.messagingSenderId as? String
    actual val gaTrackingId: String? get() = js.measurementId as? String
    
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
        
        actual fun setApiKey(apiKey: String): Builder { _apiKey = apiKey; return this }
        actual fun setApplicationId(applicationId: String): Builder { _applicationId = applicationId; return this }
        actual fun setDatabaseUrl(databaseUrl: String?): Builder { _databaseUrl = databaseUrl; return this }
        actual fun setProjectId(projectId: String?): Builder { _projectId = projectId; return this }
        actual fun setStorageBucket(storageBucket: String?): Builder { _storageBucket = storageBucket; return this }
        actual fun setGcmSenderId(gcmSenderId: String?): Builder { _gcmSenderId = gcmSenderId; return this }
        actual fun setGaTrackingId(gaTrackingId: String?): Builder { _gaTrackingId = gaTrackingId; return this }
        
        actual fun build(): FirebaseOptions {
            val config = js("{}")
            config.apiKey = _apiKey
            config.appId = _applicationId
            _databaseUrl?.let { config.databaseURL = it }
            _projectId?.let { config.projectId = it }
            _storageBucket?.let { config.storageBucket = it }
            _gcmSenderId?.let { config.messagingSenderId = it }
            _gaTrackingId?.let { config.measurementId = it }
            return FirebaseOptions(config)
        }
    }
}

