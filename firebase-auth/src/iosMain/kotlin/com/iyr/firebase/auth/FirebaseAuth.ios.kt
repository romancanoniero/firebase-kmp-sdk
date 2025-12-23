package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseAuth.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseAuth internal constructor(
    internal val ios: FIRAuth
) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = FirebaseAuth(FIRAuth.auth())
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = FirebaseAuth(FIRAuth.authWithApp(app.ios))
    }
    
    actual val currentUser: FirebaseUser?
        get() = ios.currentUser?.let { FirebaseUser(it) }
    
    actual val languageCode: String?
        get() = ios.languageCode
    
    actual fun setLanguageCode(languageCode: String?) {
        ios.setLanguageCode(languageCode)
    }
    
    actual fun useAppLanguage() = ios.useAppLanguage()
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.signInWithEmail(email, password = password) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.signInWithCredential(credential.ios) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual suspend fun signInAnonymously(): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.signInAnonymouslyWithCompletion { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual suspend fun signInWithCustomToken(token: String): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.signInWithCustomToken(token) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.createUserWithEmail(email, password = password) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual fun signOut() {
        try { ios.signOut(null) } catch (_: Exception) {}
    }
    
    actual suspend fun sendPasswordResetEmail(email: String): Unit =
        suspendCancellableCoroutine { cont ->
            ios.sendPasswordResetWithEmail(email) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    
    actual suspend fun confirmPasswordReset(code: String, newPassword: String): Unit =
        suspendCancellableCoroutine { cont ->
            ios.confirmPasswordResetWithCode(code, newPassword = newPassword) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    
    actual suspend fun verifyPasswordResetCode(code: String): String =
        suspendCancellableCoroutine { cont ->
            ios.verifyPasswordResetCode(code) { email, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(email ?: "")
            }
        }
    
    actual suspend fun applyActionCode(code: String): Unit =
        suspendCancellableCoroutine { cont ->
            ios.applyActionCode(code) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    
    actual suspend fun checkActionCode(code: String): ActionCodeResult =
        suspendCancellableCoroutine { cont ->
            ios.checkActionCode(code) { info, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(ActionCodeResult(info!!))
            }
        }
    
    private var authStateHandle: FIRAuthStateDidChangeListenerHandle? = null
    
    actual fun addAuthStateListener(listener: AuthStateListener) {
        authStateHandle = ios.addAuthStateDidChangeListener { _, _ ->
            listener.onAuthStateChanged(this)
        }
    }
    
    actual fun removeAuthStateListener(listener: AuthStateListener) {
        authStateHandle?.let { ios.removeAuthStateDidChangeListener(it) }
    }
    
    actual val authStateChanges: Flow<FirebaseUser?> = callbackFlow {
        val handle = ios.addAuthStateDidChangeListener { _, user ->
            trySend(user?.let { FirebaseUser(it) })
        }
        awaitClose { ios.removeAuthStateDidChangeListener(handle) }
    }
    
    private var idTokenHandle: FIRIDTokenDidChangeListenerHandle? = null
    
    actual fun addIdTokenListener(listener: IdTokenListener) {
        idTokenHandle = ios.addIDTokenDidChangeListener { _, _ ->
            listener.onIdTokenChanged(this)
        }
    }
    
    actual fun removeIdTokenListener(listener: IdTokenListener) {
        idTokenHandle?.let { ios.removeIDTokenDidChangeListener(it) }
    }
    
    actual val idTokenChanges: Flow<FirebaseUser?> = callbackFlow {
        val handle = ios.addIDTokenDidChangeListener { _, user ->
            trySend(user?.let { FirebaseUser(it) })
        }
        awaitClose { ios.removeIDTokenDidChangeListener(handle) }
    }
    
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult =
        suspendCancellableCoroutine { cont ->
            ios.fetchSignInMethodsForEmail(email) { methods, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(SignInMethodQueryResult(methods))
            }
        }
    
    actual suspend fun updateCurrentUser(user: FirebaseUser): Unit =
        suspendCancellableCoroutine { cont ->
            ios.updateCurrentUser(user.ios) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    
    actual fun useEmulator(host: String, port: Int) {
        ios.useEmulatorWithHost(host, port.toLong())
    }
}

actual interface AuthStateListener {
    actual fun onAuthStateChanged(auth: FirebaseAuth)
}

actual interface IdTokenListener {
    actual fun onIdTokenChanged(auth: FirebaseAuth)
}

private fun NSError.toException() = FirebaseAuthException(
    message = localizedDescription,
    errorCode = code.toString()
)

