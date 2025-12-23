package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.Promise

// Firebase Auth JS SDK external declarations
@JsModule("firebase/auth")
@JsNonModule
external object FirebaseAuthJS {
    fun getAuth(app: dynamic = definedExternally): dynamic
    fun signInWithEmailAndPassword(auth: dynamic, email: String, password: String): Promise<dynamic>
    fun createUserWithEmailAndPassword(auth: dynamic, email: String, password: String): Promise<dynamic>
    fun signInAnonymously(auth: dynamic): Promise<dynamic>
    fun signInWithCustomToken(auth: dynamic, token: String): Promise<dynamic>
    fun signInWithCredential(auth: dynamic, credential: dynamic): Promise<dynamic>
    fun signOut(auth: dynamic): Promise<dynamic>
    fun sendPasswordResetEmail(auth: dynamic, email: String): Promise<dynamic>
    fun confirmPasswordReset(auth: dynamic, code: String, newPassword: String): Promise<dynamic>
    fun verifyPasswordResetCode(auth: dynamic, code: String): Promise<String>
    fun applyActionCode(auth: dynamic, code: String): Promise<dynamic>
    fun checkActionCode(auth: dynamic, code: String): Promise<dynamic>
    fun fetchSignInMethodsForEmail(auth: dynamic, email: String): Promise<Array<String>>
    fun onAuthStateChanged(auth: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onIdTokenChanged(auth: dynamic, callback: (dynamic) -> Unit): () -> Unit
}

actual class FirebaseAuth internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = FirebaseAuth(FirebaseAuthJS.getAuth())
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = FirebaseAuth(FirebaseAuthJS.getAuth(app.js))
    }
    
    actual val currentUser: FirebaseUser? get() = js.currentUser?.let { FirebaseUser(it) }
    actual val languageCode: String? get() = js.languageCode as? String
    actual fun setLanguageCode(languageCode: String?) { js.languageCode = languageCode }
    actual fun useAppLanguage() { js.useDeviceLanguage() }
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        val result = FirebaseAuthJS.signInWithEmailAndPassword(js, email, password).await()
        return AuthResult(result)
    }
    
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        val result = FirebaseAuthJS.signInWithCredential(js, credential.js).await()
        return AuthResult(result)
    }
    
    actual suspend fun signInAnonymously(): AuthResult {
        val result = FirebaseAuthJS.signInAnonymously(js).await()
        return AuthResult(result)
    }
    
    actual suspend fun signInWithCustomToken(token: String): AuthResult {
        val result = FirebaseAuthJS.signInWithCustomToken(js, token).await()
        return AuthResult(result)
    }
    
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        val result = FirebaseAuthJS.createUserWithEmailAndPassword(js, email, password).await()
        return AuthResult(result)
    }
    
    actual fun signOut() { 
        FirebaseAuthJS.signOut(js)
    }
    
    actual suspend fun sendPasswordResetEmail(email: String) {
        FirebaseAuthJS.sendPasswordResetEmail(js, email).await()
    }
    
    actual suspend fun confirmPasswordReset(code: String, newPassword: String) {
        FirebaseAuthJS.confirmPasswordReset(js, code, newPassword).await()
    }
    
    actual suspend fun verifyPasswordResetCode(code: String): String {
        return FirebaseAuthJS.verifyPasswordResetCode(js, code).await()
    }
    
    actual suspend fun applyActionCode(code: String) {
        FirebaseAuthJS.applyActionCode(js, code).await()
    }
    
    actual suspend fun checkActionCode(code: String): ActionCodeResult {
        val result = FirebaseAuthJS.checkActionCode(js, code).await()
        return ActionCodeResult(result)
    }
    
    private var authStateListeners = mutableListOf<AuthStateListener>()
    private var idTokenListeners = mutableListOf<IdTokenListener>()
    
    actual fun addAuthStateListener(listener: AuthStateListener) {
        authStateListeners.add(listener)
        FirebaseAuthJS.onAuthStateChanged(js) { 
            authStateListeners.forEach { it.onAuthStateChanged(this) }
        }
    }
    
    actual fun removeAuthStateListener(listener: AuthStateListener) {
        authStateListeners.remove(listener)
    }
    
    actual val authStateChanges: Flow<FirebaseUser?> = callbackFlow {
        val unsubscribe = FirebaseAuthJS.onAuthStateChanged(js) { user ->
            trySend(user?.let { FirebaseUser(it) })
        }
        awaitClose { unsubscribe() }
    }
    
    actual fun addIdTokenListener(listener: IdTokenListener) {
        idTokenListeners.add(listener)
    }
    
    actual fun removeIdTokenListener(listener: IdTokenListener) {
        idTokenListeners.remove(listener)
    }
    
    actual val idTokenChanges: Flow<FirebaseUser?> = callbackFlow {
        val unsubscribe = FirebaseAuthJS.onIdTokenChanged(js) { user ->
            trySend(user?.let { FirebaseUser(it) })
        }
        awaitClose { unsubscribe() }
    }
    
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult {
        val methods = FirebaseAuthJS.fetchSignInMethodsForEmail(js, email).await()
        return SignInMethodQueryResult(methods.toList())
    }
    
    actual suspend fun updateCurrentUser(user: FirebaseUser) {
        js.updateCurrentUser(user.js)
    }
    
    actual fun useEmulator(host: String, port: Int) {
        js.useEmulator("http://$host:$port")
    }
}

actual interface AuthStateListener { 
    actual fun onAuthStateChanged(auth: FirebaseAuth) 
}

actual interface IdTokenListener { 
    actual fun onIdTokenChanged(auth: FirebaseAuth) 
}

// Helper extension for Promise.await()
private suspend fun <T> Promise<T>.await(): T = kotlinx.coroutines.await()
