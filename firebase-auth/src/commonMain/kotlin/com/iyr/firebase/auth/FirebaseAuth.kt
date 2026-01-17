package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow

/**
 * FirebaseAuth - API de autenticación de Firebase
 * 
 * Replica la API del Firebase Android SDK con paridad 100%.
 * 
 * @see com.google.firebase.auth.FirebaseAuth
 */
expect class FirebaseAuth {
    
    companion object {
        fun getInstance(): FirebaseAuth
        fun getInstance(app: FirebaseApp): FirebaseAuth
    }
    
    // Current user
    val currentUser: FirebaseUser?
    val languageCode: String?
    
    fun setLanguageCode(languageCode: String?)
    fun useAppLanguage()
    
    // Sign in methods
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithCredential(credential: AuthCredential): AuthResult
    suspend fun signInAnonymously(): AuthResult
    suspend fun signInWithCustomToken(token: String): AuthResult
    
    // Create user
    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult
    
    // Sign out
    fun signOut()
    
    // Password reset
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun confirmPasswordReset(code: String, newPassword: String)
    suspend fun verifyPasswordResetCode(code: String): String
    
    // Email verification
    suspend fun applyActionCode(code: String)
    suspend fun checkActionCode(code: String): ActionCodeResult
    
    // Auth state
    fun addAuthStateListener(listener: AuthStateListener)
    fun removeAuthStateListener(listener: AuthStateListener)
    val authStateChanges: Flow<FirebaseUser?>
    
    // ID token
    fun addIdTokenListener(listener: IdTokenListener)
    fun removeIdTokenListener(listener: IdTokenListener)
    val idTokenChanges: Flow<FirebaseUser?>
    
    // Fetch sign-in methods
    suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult
    
    // Update current user
    suspend fun updateCurrentUser(user: FirebaseUser)
    
    // Emulator
    fun useEmulator(host: String, port: Int)
    
    // Custom Claims
    /**
     * Obtiene los Custom Claims del usuario actual desde el ID Token
     * 
     * Los Custom Claims son metadata adicional asignada a usuarios via Firebase Admin SDK
     * o Cloud Functions. Útil para roles, permisos, company_id, etc.
     * 
     * @param forceRefresh Si true, fuerza un refresh del token desde el servidor.
     *                     Si false, usa el token cacheado si existe.
     * @return Map con los custom claims o null si no hay usuario autenticado
     * 
     * Ejemplo de uso:
     * ```kotlin
     * val claims = firebaseAuth.getCustomClaims(forceRefresh = false)
     * val companyId = claims?.get("company_id") as? String
     * val roles = claims?.get("roles") as? List<String>
     * ```
     * 
     * @see <a href="https://firebase.google.com/docs/auth/admin/custom-claims">Firebase Custom Claims</a>
     */
    suspend fun getCustomClaims(forceRefresh: Boolean = false): Map<String, Any>?
}

/**
 * AuthStateListener - Escucha cambios en el estado de autenticación
 */
expect interface AuthStateListener {
    fun onAuthStateChanged(auth: FirebaseAuth)
}

/**
 * IdTokenListener - Escucha cambios en el ID token
 */
expect interface IdTokenListener {
    fun onIdTokenChanged(auth: FirebaseAuth)
}






