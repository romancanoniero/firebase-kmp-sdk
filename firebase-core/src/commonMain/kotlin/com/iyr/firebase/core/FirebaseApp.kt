package com.iyr.firebase.core

/**
 * FirebaseApp - Punto de entrada principal para Firebase
 * 
 * Replica la API del Firebase Android SDK:
 * - FirebaseApp.getInstance() para obtener la instancia por defecto
 * - FirebaseApp.initializeApp() para inicializar con opciones personalizadas
 * 
 * USO:
 * ```kotlin
 * // Obtener instancia por defecto (ya inicializada por el SDK nativo)
 * val app = FirebaseApp.getInstance()
 * 
 * // Inicializar con opciones personalizadas
 * val options = FirebaseOptions.Builder()
 *     .setApiKey("...")
 *     .setApplicationId("...")
 *     .build()
 * val customApp = FirebaseApp.initializeApp(options, "myApp")
 * ```
 * 
 * @see com.google.firebase.FirebaseApp (Android SDK)
 */
expect class FirebaseApp {
    
    companion object {
        /**
         * Obtiene la instancia por defecto de FirebaseApp
         * 
         * @return La instancia por defecto de FirebaseApp
         * @throws IllegalStateException si Firebase no ha sido inicializado
         */
        fun getInstance(): FirebaseApp
        
        /**
         * Obtiene una instancia de FirebaseApp por nombre
         * 
         * @param name Nombre de la instancia
         * @return La instancia de FirebaseApp con ese nombre
         * @throws IllegalStateException si no existe una instancia con ese nombre
         */
        fun getInstance(name: String): FirebaseApp
        
        /**
         * Inicializa Firebase con las opciones por defecto
         * 
         * En Android: Lee google-services.json
         * En iOS: Lee GoogleService-Info.plist
         * En JS: Requiere configuración manual
         * 
         * @return La instancia de FirebaseApp inicializada
         */
        fun initializeApp(): FirebaseApp
        
        /**
         * Inicializa Firebase con opciones personalizadas
         * 
         * @param options Configuración de Firebase
         * @return La instancia de FirebaseApp inicializada
         */
        fun initializeApp(options: FirebaseOptions): FirebaseApp
        
        /**
         * Inicializa Firebase con opciones personalizadas y nombre
         * 
         * @param options Configuración de Firebase
         * @param name Nombre para la instancia
         * @return La instancia de FirebaseApp inicializada
         */
        fun initializeApp(options: FirebaseOptions, name: String): FirebaseApp
        
        /**
         * Obtiene todas las instancias de FirebaseApp
         * 
         * @return Lista de todas las instancias
         */
        fun getApps(): List<FirebaseApp>
    }
    
    /**
     * Obtiene el nombre de esta instancia de FirebaseApp
     * 
     * @return Nombre de la instancia ("[DEFAULT]" para la instancia por defecto)
     */
    fun getName(): String
    
    /**
     * Obtiene las opciones de configuración de esta instancia
     * 
     * @return Las opciones de Firebase
     */
    fun getOptions(): FirebaseOptions
    
    /**
     * Elimina esta instancia de FirebaseApp
     * 
     * Después de llamar a este método, la instancia no puede ser usada.
     */
    fun delete()
    
    /**
     * Verifica si los datos automáticos están habilitados
     * 
     * @return true si está habilitado
     */
    fun isDataCollectionDefaultEnabled(): Boolean
    
    /**
     * Habilita o deshabilita la recolección automática de datos
     * 
     * @param enabled true para habilitar, false para deshabilitar
     */
    fun setDataCollectionDefaultEnabled(enabled: Boolean)
}






