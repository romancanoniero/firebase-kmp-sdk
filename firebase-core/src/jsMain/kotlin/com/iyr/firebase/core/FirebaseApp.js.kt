@file:JsModule("firebase/app")
@file:JsNonModule

package com.iyr.firebase.core

import kotlin.js.Json

/**
 * FirebaseApp - Implementación JavaScript
 * 
 * Usa el Firebase JS SDK via interop.
 */

// External declarations for Firebase JS SDK
external fun initializeApp(options: Json): dynamic
external fun initializeApp(options: Json, name: String): dynamic
external fun getApp(): dynamic
external fun getApp(name: String): dynamic
external fun getApps(): Array<dynamic>
external fun deleteApp(app: dynamic): dynamic

actual class FirebaseApp internal constructor(
    internal val js: dynamic
) {
    
    actual companion object {
        
        actual fun getInstance(): FirebaseApp {
            return FirebaseApp(getApp())
        }
        
        actual fun getInstance(name: String): FirebaseApp {
            return FirebaseApp(getApp(name))
        }
        
        actual fun initializeApp(): FirebaseApp {
            throw UnsupportedOperationException(
                "En JS debes usar initializeApp(options) con la configuración de Firebase"
            )
        }
        
        actual fun initializeApp(options: FirebaseOptions): FirebaseApp {
            val config = js("{}")
            config.apiKey = options.apiKey
            config.appId = options.applicationId
            options.databaseUrl?.let { config.databaseURL = it }
            options.projectId?.let { config.projectId = it }
            options.storageBucket?.let { config.storageBucket = it }
            options.gcmSenderId?.let { config.messagingSenderId = it }
            
            val app = initializeApp(config.unsafeCast<Json>())
            return FirebaseApp(app)
        }
        
        actual fun initializeApp(options: FirebaseOptions, name: String): FirebaseApp {
            val config = js("{}")
            config.apiKey = options.apiKey
            config.appId = options.applicationId
            options.databaseUrl?.let { config.databaseURL = it }
            options.projectId?.let { config.projectId = it }
            options.storageBucket?.let { config.storageBucket = it }
            options.gcmSenderId?.let { config.messagingSenderId = it }
            
            val app = initializeApp(config.unsafeCast<Json>(), name)
            return FirebaseApp(app)
        }
        
        actual fun getApps(): List<FirebaseApp> {
            return getApps().map { FirebaseApp(it) }
        }
    }
    
    actual fun getName(): String = js.name as String
    
    actual fun getOptions(): FirebaseOptions {
        val opts = js.options
        return FirebaseOptions.Builder()
            .setApiKey(opts.apiKey as String)
            .setApplicationId(opts.appId as String)
            .setDatabaseUrl(opts.databaseURL as? String)
            .setProjectId(opts.projectId as? String)
            .setStorageBucket(opts.storageBucket as? String)
            .setGcmSenderId(opts.messagingSenderId as? String)
            .build()
    }
    
    actual fun delete() {
        deleteApp(js)
    }
    
    actual fun isDataCollectionDefaultEnabled(): Boolean = true
    
    actual fun setDataCollectionDefaultEnabled(enabled: Boolean) {
        // No-op en JS - se maneja diferente
    }
}

