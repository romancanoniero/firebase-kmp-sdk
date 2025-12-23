package com.iyr.firebase.core

import android.content.Context

/**
 * FirebaseApp - Implementación Android
 * 
 * Delega directamente al Firebase Android SDK.
 * 
 * @see com.google.firebase.FirebaseApp
 */
actual class FirebaseApp internal constructor(
    val android: com.google.firebase.FirebaseApp
) {
    
    actual companion object {
        
        /**
         * Obtiene la instancia por defecto de FirebaseApp
         */
        actual fun getInstance(): FirebaseApp {
            return FirebaseApp(com.google.firebase.FirebaseApp.getInstance())
        }
        
        /**
         * Obtiene una instancia de FirebaseApp por nombre
         */
        actual fun getInstance(name: String): FirebaseApp {
            return FirebaseApp(com.google.firebase.FirebaseApp.getInstance(name))
        }
        
        /**
         * Inicializa Firebase con las opciones por defecto (lee google-services.json)
         * 
         * NOTA: En Android, Firebase se inicializa automáticamente si tienes
         * el plugin de google-services configurado. Este método es para
         * inicialización manual.
         * 
         * @param context Context de Android (Application o Activity)
         */
        fun initializeApp(context: Context): FirebaseApp {
            val app = com.google.firebase.FirebaseApp.initializeApp(context)
                ?: throw IllegalStateException("Firebase initialization failed")
            return FirebaseApp(app)
        }
        
        /**
         * Inicializa Firebase - versión sin contexto para API común
         * 
         * ADVERTENCIA: En Android DEBE llamarse initializeApp(context) primero,
         * o usar el plugin google-services para inicialización automática.
         */
        actual fun initializeApp(): FirebaseApp {
            return getInstance()
        }
        
        /**
         * Inicializa Firebase con opciones personalizadas
         */
        actual fun initializeApp(options: FirebaseOptions): FirebaseApp {
            throw UnsupportedOperationException(
                "En Android, usa initializeApp(context, options) en su lugar"
            )
        }
        
        /**
         * Inicializa Firebase con opciones personalizadas y contexto
         */
        fun initializeApp(context: Context, options: FirebaseOptions): FirebaseApp {
            val app = com.google.firebase.FirebaseApp.initializeApp(context, options.android)
            return FirebaseApp(app)
        }
        
        /**
         * Inicializa Firebase con opciones personalizadas y nombre
         */
        actual fun initializeApp(options: FirebaseOptions, name: String): FirebaseApp {
            throw UnsupportedOperationException(
                "En Android, usa initializeApp(context, options, name) en su lugar"
            )
        }
        
        /**
         * Inicializa Firebase con contexto, opciones y nombre
         */
        fun initializeApp(context: Context, options: FirebaseOptions, name: String): FirebaseApp {
            val app = com.google.firebase.FirebaseApp.initializeApp(context, options.android, name)
            return FirebaseApp(app)
        }
        
        /**
         * Obtiene todas las instancias de FirebaseApp
         */
        actual fun getApps(): List<FirebaseApp> {
            return com.google.firebase.FirebaseApp.getApps(
                // Usar el contexto de la app por defecto
                com.google.firebase.FirebaseApp.getInstance().applicationContext
            ).map { FirebaseApp(it) }
        }
    }
    
    /**
     * Obtiene el nombre de esta instancia
     */
    actual fun getName(): String = android.name
    
    /**
     * Obtiene las opciones de configuración
     */
    actual fun getOptions(): FirebaseOptions = FirebaseOptions(android.options)
    
    /**
     * Elimina esta instancia de FirebaseApp
     */
    actual fun delete() = android.delete()
    
    /**
     * Verifica si la recolección de datos está habilitada
     */
    actual fun isDataCollectionDefaultEnabled(): Boolean = 
        android.isDataCollectionDefaultEnabled
    
    /**
     * Habilita o deshabilita la recolección automática de datos
     */
    actual fun setDataCollectionDefaultEnabled(enabled: Boolean) {
        android.setDataCollectionDefaultEnabled(enabled)
    }
}

/**
 * Extension para obtener el objeto nativo de Firebase Android SDK
 * 
 * Útil cuando necesitas acceder a funcionalidad específica de Android
 * que no está expuesta en la API común.
 */
val FirebaseApp.native: com.google.firebase.FirebaseApp
    get() = android

