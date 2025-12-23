package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth as AndroidFirebaseAuth

actual class FirebaseAuth internal constructor(
    internal val android: AndroidFirebaseAuth
) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = 
            FirebaseAuth(AndroidFirebaseAuth.getInstance())
        
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = 
            FirebaseAuth(AndroidFirebaseAuth.getInstance(app.android))
    }
    
    actual val currentUser: FirebaseUser?
        get() = android.currentUser?.toKmp()
    
    actual val languageCode: String?
        get() = android.languageCode
    
    actual fun setLanguageCode(languageCode: String?) {
        android.setLanguageCode(languageCode ?: "")
    }
    
    actual fun useAppLanguage() = android.useAppLanguage()
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return android.signInWithEmailAndPassword(email, password).await().toKmp()
    }
    
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        return android.signInWithCredential(credential.android).await().toKmp()
    }
    
    actual suspend fun signInAnonymously(): AuthResult {
        return android.signInAnonymously().await().toKmp()
    }
    
    actual suspend fun signInWithCustomToken(token: String): AuthResult {
        return android.signInWithCustomToken(token).await().toKmp()
    }
    
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        return android.createUserWithEmailAndPassword(email, password).await().toKmp()
    }
    
    actual fun signOut() = android.signOut()
    
    actual suspend fun sendPasswordResetEmail(email: String) {
        android.sendPasswordResetEmail(email).await()
    }
    
    actual suspend fun confirmPasswordReset(code: String, newPassword: String) {
        android.confirmPasswordReset(code, newPassword).await()
    }
    
    actual suspend fun verifyPasswordResetCode(code: String): String {
        return android.verifyPasswordResetCode(code).await()
    }
    
    actual suspend fun applyActionCode(code: String) {
        android.applyActionCode(code).await()
    }
    
    actual suspend fun checkActionCode(code: String): ActionCodeResult {
        return android.checkActionCode(code).await().toKmp()
    }
    
    actual fun addAuthStateListener(listener: AuthStateListener) {
        android.addAuthStateListener { listener.onAuthStateChanged(this) }
    }
    
    actual fun removeAuthStateListener(listener: AuthStateListener) {
        // Note: Android SDK requires the same listener instance
    }
    
    actual val authStateChanges: Flow<FirebaseUser?> = callbackFlow {
        val listener = AndroidFirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toKmp())
        }
        android.addAuthStateListener(listener)
        awaitClose { android.removeAuthStateListener(listener) }
    }
    
    actual fun addIdTokenListener(listener: IdTokenListener) {
        val androidListener = AndroidFirebaseAuth.IdTokenListener { _ -> 
            listener.onIdTokenChanged(this) 
        }
        android.addIdTokenListener(androidListener)
    }
    
    actual fun removeIdTokenListener(listener: IdTokenListener) {
        // Note: Android SDK requires the same listener instance
    }
    
    actual val idTokenChanges: Flow<FirebaseUser?> = callbackFlow {
        val androidListener = AndroidFirebaseAuth.IdTokenListener { auth ->
            trySend(auth.currentUser?.toKmp())
        }
        android.addIdTokenListener(androidListener)
        awaitClose { android.removeIdTokenListener(androidListener) }
    }
    
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult {
        @Suppress("DEPRECATION")
        return android.fetchSignInMethodsForEmail(email).await().toKmp()
    }
    
    actual suspend fun updateCurrentUser(user: FirebaseUser) {
        android.updateCurrentUser(user.android).await()
    }
    
    actual fun useEmulator(host: String, port: Int) {
        android.useEmulator(host, port)
    }
}

actual interface AuthStateListener {
    actual fun onAuthStateChanged(auth: FirebaseAuth)
}

actual interface IdTokenListener {
    actual fun onIdTokenChanged(auth: FirebaseAuth)
}

// Extension functions
private fun com.google.firebase.auth.FirebaseUser.toKmp() = FirebaseUser(this)
private fun com.google.firebase.auth.AuthResult.toKmp() = AuthResult(this)
private fun com.google.firebase.auth.SignInMethodQueryResult.toKmp() = SignInMethodQueryResult(this)
private fun com.google.firebase.auth.ActionCodeResult.toKmp() = ActionCodeResult(this)

