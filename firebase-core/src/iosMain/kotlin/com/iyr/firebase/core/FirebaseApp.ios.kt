package com.iyr.firebase.core

import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseCore.FIROptions

/**
 * FirebaseApp - Implementación iOS
 * 
 * Usa Kotlin/Native cinterop para llamar directamente al Firebase iOS SDK.
 * FIRApp es la clase Objective-C equivalente a FirebaseApp.
 */
actual class FirebaseApp internal constructor(
    val ios: FIRApp
) {
    
    actual companion object {
        
        actual fun getInstance(): FirebaseApp {
            val app = FIRApp.defaultApp() 
                ?: throw IllegalStateException("Firebase not initialized. Call FirebaseApp.configure() first.")
            return FirebaseApp(app)
        }
        
        actual fun getInstance(name: String): FirebaseApp {
            val app = FIRApp.appNamed(name)
                ?: throw IllegalStateException("Firebase app '$name' not found")
            return FirebaseApp(app)
        }
        
        actual fun initializeApp(): FirebaseApp {
            FIRApp.configure()
            return getInstance()
        }
        
        actual fun initializeApp(options: FirebaseOptions): FirebaseApp {
            FIRApp.configureWithOptions(options.ios)
            return getInstance()
        }
        
        actual fun initializeApp(options: FirebaseOptions, name: String): FirebaseApp {
            FIRApp.configureWithName(name, options = options.ios)
            return getInstance(name)
        }
        
        actual fun getApps(): List<FirebaseApp> {
            val appsMap = FIRApp.allApps() ?: return emptyList()
            @Suppress("UNCHECKED_CAST")
            return (appsMap as Map<String, FIRApp>).values.map { FirebaseApp(it) }
        }
    }
    
    actual fun getName(): String = ios.name
    
    actual fun getOptions(): FirebaseOptions = FirebaseOptions(ios.options)
    
    actual fun delete() {
        ios.deleteApp { /* completion handler */ }
    }
    
    actual fun isDataCollectionDefaultEnabled(): Boolean = 
        ios.isDataCollectionDefaultEnabled()
    
    actual fun setDataCollectionDefaultEnabled(enabled: Boolean) {
        ios.setDataCollectionDefaultEnabled(enabled)
    }
}

/**
 * Extension para acceder al objeto nativo iOS
 */
val FirebaseApp.native: FIRApp
    get() = ios

