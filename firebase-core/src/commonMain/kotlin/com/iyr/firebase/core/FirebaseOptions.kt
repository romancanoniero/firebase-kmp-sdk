package com.iyr.firebase.core

/**
 * FirebaseOptions - Configuración para inicializar FirebaseApp
 * 
 * Replica la API del Firebase Android SDK.
 * Contiene las credenciales y configuración necesarias para conectar con Firebase.
 * 
 * USO:
 * ```kotlin
 * val options = FirebaseOptions.Builder()
 *     .setApiKey("AIzaSy...")
 *     .setApplicationId("1:123456789:android:abc123")
 *     .setDatabaseUrl("https://myapp.firebaseio.com")
 *     .setProjectId("my-project")
 *     .setStorageBucket("my-project.appspot.com")
 *     .setGcmSenderId("123456789")
 *     .build()
 * 
 * val app = FirebaseApp.initializeApp(options)
 * ```
 * 
 * @see com.google.firebase.FirebaseOptions (Android SDK)
 */
expect class FirebaseOptions {
    
    /**
     * API Key de Firebase
     * 
     * Se obtiene de la Firebase Console o del archivo google-services.json
     */
    val apiKey: String
    
    /**
     * Application ID (también conocido como App ID o mobilesdk_app_id)
     * 
     * Formato: "1:123456789:android:abc123def456"
     */
    val applicationId: String
    
    /**
     * URL de la base de datos Realtime Database
     * 
     * Formato: "https://project-id.firebaseio.com" o 
     *          "https://project-id-default-rtdb.firebaseio.com"
     */
    val databaseUrl: String?
    
    /**
     * ID del proyecto de Firebase/Google Cloud
     * 
     * Ejemplo: "my-firebase-project"
     */
    val projectId: String?
    
    /**
     * Bucket de Cloud Storage
     * 
     * Formato: "project-id.appspot.com"
     */
    val storageBucket: String?
    
    /**
     * GCM Sender ID para Cloud Messaging
     * 
     * Es el project_number del proyecto de Firebase
     */
    val gcmSenderId: String?
    
    /**
     * GA Tracking ID para Google Analytics
     */
    val gaTrackingId: String?
    
    /**
     * Builder para crear FirebaseOptions
     * 
     * Patrón Builder idéntico al del SDK de Android
     */
    class Builder {
        /**
         * Crea un Builder vacío
         */
        constructor()
        
        /**
         * Crea un Builder copiando las opciones existentes
         */
        constructor(options: FirebaseOptions)
        
        /**
         * Establece la API Key
         */
        fun setApiKey(apiKey: String): Builder
        
        /**
         * Establece el Application ID
         */
        fun setApplicationId(applicationId: String): Builder
        
        /**
         * Establece la URL de Realtime Database
         */
        fun setDatabaseUrl(databaseUrl: String?): Builder
        
        /**
         * Establece el Project ID
         */
        fun setProjectId(projectId: String?): Builder
        
        /**
         * Establece el Storage Bucket
         */
        fun setStorageBucket(storageBucket: String?): Builder
        
        /**
         * Establece el GCM Sender ID
         */
        fun setGcmSenderId(gcmSenderId: String?): Builder
        
        /**
         * Establece el GA Tracking ID
         */
        fun setGaTrackingId(gaTrackingId: String?): Builder
        
        /**
         * Construye las FirebaseOptions
         * 
         * @return FirebaseOptions con la configuración establecida
         * @throws IllegalStateException si faltan campos requeridos (apiKey, applicationId)
         */
        fun build(): FirebaseOptions
    }
}






