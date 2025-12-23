@file:JsModule("firebase/auth")
package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

// External JS declarations
external fun getAuth(): dynamic
external fun signInWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic
external fun createUserWithEmailAndPassword(auth: dynamic, email: String, password: String): dynamic
external fun signInAnonymously(auth: dynamic): dynamic
external fun signInWithCustomToken(auth: dynamic, token: String): dynamic
external fun signOut(auth: dynamic): dynamic
external fun sendPasswordResetEmail(auth: dynamic, email: String): dynamic
external fun onAuthStateChanged(auth: dynamic, callback: (dynamic) -> Unit): dynamic

actual class FirebaseAuth internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = FirebaseAuth(getAuth())
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = FirebaseAuth(getAuth())
    }
    
    actual val currentUser: FirebaseUser? get() = js.currentUser?.let { FirebaseUser(it) }
    actual val languageCode: String? get() = js.languageCode as? String
    actual fun setLanguageCode(languageCode: String?) { js.languageCode = languageCode }
    actual fun useAppLanguage() { js.useDeviceLanguage() }
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult = TODO()
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult = TODO()
    actual suspend fun signInAnonymously(): AuthResult = TODO()
    actual suspend fun signInWithCustomToken(token: String): AuthResult = TODO()
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult = TODO()
    actual fun signOut() { signOut(js) }
    actual suspend fun sendPasswordResetEmail(email: String) { TODO() }
    actual suspend fun confirmPasswordReset(code: String, newPassword: String) { TODO() }
    actual suspend fun verifyPasswordResetCode(code: String): String = TODO()
    actual suspend fun applyActionCode(code: String) { TODO() }
    actual suspend fun checkActionCode(code: String): ActionCodeResult = TODO()
    actual fun addAuthStateListener(listener: AuthStateListener) {}
    actual fun removeAuthStateListener(listener: AuthStateListener) {}
    actual val authStateChanges: Flow<FirebaseUser?> = MutableStateFlow(null)
    actual fun addIdTokenListener(listener: IdTokenListener) {}
    actual fun removeIdTokenListener(listener: IdTokenListener) {}
    actual val idTokenChanges: Flow<FirebaseUser?> = MutableStateFlow(null)
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult = TODO()
    actual suspend fun updateCurrentUser(user: FirebaseUser) { TODO() }
    actual fun useEmulator(host: String, port: Int) {}
}

actual interface AuthStateListener { actual fun onAuthStateChanged(auth: FirebaseAuth) }
actual interface IdTokenListener { actual fun onIdTokenChanged(auth: FirebaseAuth) }

