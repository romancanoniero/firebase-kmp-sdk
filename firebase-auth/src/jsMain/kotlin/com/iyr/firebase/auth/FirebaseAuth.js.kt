package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class FirebaseAuth internal constructor(private val jsAuth: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = FirebaseAuth(null)
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = FirebaseAuth(null)
    }
    
    actual val currentUser: FirebaseUser? get() = null
    actual val languageCode: String? get() = null
    actual fun setLanguageCode(languageCode: String?) {}
    actual fun useAppLanguage() {}
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult = AuthResult()
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult = AuthResult()
    actual suspend fun signInAnonymously(): AuthResult = AuthResult()
    actual suspend fun signInWithCustomToken(token: String): AuthResult = AuthResult()
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult = AuthResult()
    actual fun signOut() {}
    actual suspend fun sendPasswordResetEmail(email: String) {}
    actual suspend fun confirmPasswordReset(code: String, newPassword: String) {}
    actual suspend fun verifyPasswordResetCode(code: String): String = ""
    actual suspend fun applyActionCode(code: String) {}
    actual suspend fun checkActionCode(code: String): ActionCodeResult = ActionCodeResult()
    actual fun addAuthStateListener(listener: AuthStateListener) {}
    actual fun removeAuthStateListener(listener: AuthStateListener) {}
    actual val authStateChanges: Flow<FirebaseUser?> = MutableStateFlow(null)
    actual fun addIdTokenListener(listener: IdTokenListener) {}
    actual fun removeIdTokenListener(listener: IdTokenListener) {}
    actual val idTokenChanges: Flow<FirebaseUser?> = MutableStateFlow(null)
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult = SignInMethodQueryResult()
    actual suspend fun updateCurrentUser(user: FirebaseUser) {}
    actual fun useEmulator(host: String, port: Int) {}
}

actual interface AuthStateListener { actual fun onAuthStateChanged(auth: FirebaseAuth) }
actual interface IdTokenListener { actual fun onIdTokenChanged(auth: FirebaseAuth) }
