@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.auth

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.Promise
import kotlin.js.Date

@JsModule("firebase/auth")
@JsNonModule
private external object AuthModule {
    fun getAuth(): dynamic
    fun getAuth(app: dynamic): dynamic
    fun signInWithEmailAndPassword(auth: dynamic, email: String, password: String): Promise<dynamic>
    fun createUserWithEmailAndPassword(auth: dynamic, email: String, password: String): Promise<dynamic>
    fun signInAnonymously(auth: dynamic): Promise<dynamic>
    fun signInWithCustomToken(auth: dynamic, token: String): Promise<dynamic>
    fun signInWithCredential(auth: dynamic, credential: dynamic): Promise<dynamic>
    fun signOut(auth: dynamic): Promise<dynamic>
    fun sendPasswordResetEmail(auth: dynamic, email: String): Promise<dynamic>
    fun confirmPasswordReset(auth: dynamic, code: String, newPassword: String): Promise<dynamic>
    fun verifyPasswordResetCode(auth: dynamic, code: String): Promise<dynamic>
    fun applyActionCode(auth: dynamic, code: String): Promise<dynamic>
    fun checkActionCode(auth: dynamic, code: String): Promise<dynamic>
    fun fetchSignInMethodsForEmail(auth: dynamic, email: String): Promise<dynamic>
    fun updateCurrentUser(auth: dynamic, user: dynamic): Promise<dynamic>
    fun onAuthStateChanged(auth: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onIdTokenChanged(auth: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun connectAuthEmulator(auth: dynamic, url: String)
    fun updateProfile(user: dynamic, profile: dynamic): Promise<dynamic>
    fun updateEmail(user: dynamic, email: String): Promise<dynamic>
    fun updatePassword(user: dynamic, password: String): Promise<dynamic>
    fun sendEmailVerification(user: dynamic): Promise<dynamic>
    fun reload(user: dynamic): Promise<dynamic>
    fun deleteUser(user: dynamic): Promise<dynamic>
    fun getIdToken(user: dynamic, forceRefresh: Boolean): Promise<dynamic>
    fun getIdTokenResult(user: dynamic, forceRefresh: Boolean): Promise<dynamic>
    fun linkWithCredential(user: dynamic, credential: dynamic): Promise<dynamic>
    fun reauthenticateWithCredential(user: dynamic, credential: dynamic): Promise<dynamic>
    fun unlink(user: dynamic, providerId: String): Promise<dynamic>
    val EmailAuthProvider: dynamic
    val GoogleAuthProvider: dynamic
    val FacebookAuthProvider: dynamic
    val PhoneAuthProvider: dynamic
}

actual class FirebaseAuth internal constructor(private val jsAuth: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseAuth = FirebaseAuth(AuthModule.getAuth())
        actual fun getInstance(app: FirebaseApp): FirebaseAuth = FirebaseAuth(AuthModule.getAuth(app.js))
    }
    
    actual val currentUser: FirebaseUser? get() = jsAuth.currentUser?.let { u -> FirebaseUser(u) }
    actual val languageCode: String? get() = jsAuth.languageCode as? String
    actual fun setLanguageCode(languageCode: String?) { jsAuth.languageCode = languageCode }
    actual fun useAppLanguage() { jsAuth.useDeviceLanguage() }
    
    actual suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        val result = AuthModule.signInWithEmailAndPassword(jsAuth, email, password).await<dynamic>()
        return AuthResult(result)
    }
    
    actual suspend fun signInWithCredential(credential: AuthCredential): AuthResult {
        val result = AuthModule.signInWithCredential(jsAuth, credential.jsCredential).await<dynamic>()
        return AuthResult(result)
    }
    
    actual suspend fun signInAnonymously(): AuthResult {
        val result = AuthModule.signInAnonymously(jsAuth).await<dynamic>()
        return AuthResult(result)
    }
    
    actual suspend fun signInWithCustomToken(token: String): AuthResult {
        val result = AuthModule.signInWithCustomToken(jsAuth, token).await<dynamic>()
        return AuthResult(result)
    }
    
    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        val result = AuthModule.createUserWithEmailAndPassword(jsAuth, email, password).await<dynamic>()
        return AuthResult(result)
    }
    
    actual fun signOut() { AuthModule.signOut(jsAuth) }
    actual suspend fun sendPasswordResetEmail(email: String) { AuthModule.sendPasswordResetEmail(jsAuth, email).await<dynamic>() }
    actual suspend fun confirmPasswordReset(code: String, newPassword: String) { AuthModule.confirmPasswordReset(jsAuth, code, newPassword).await<dynamic>() }
    actual suspend fun verifyPasswordResetCode(code: String): String = AuthModule.verifyPasswordResetCode(jsAuth, code).await<dynamic>() as String
    actual suspend fun applyActionCode(code: String) { AuthModule.applyActionCode(jsAuth, code).await<dynamic>() }
    actual suspend fun checkActionCode(code: String): ActionCodeResult {
        val result = AuthModule.checkActionCode(jsAuth, code).await<dynamic>()
        return ActionCodeResult(result)
    }
    
    actual fun addAuthStateListener(listener: AuthStateListener) {
        AuthModule.onAuthStateChanged(jsAuth) { listener.onAuthStateChanged(this) }
    }
    actual fun removeAuthStateListener(listener: AuthStateListener) {}
    
    actual val authStateChanges: Flow<FirebaseUser?> = callbackFlow {
        val unsub = AuthModule.onAuthStateChanged(jsAuth) { user -> trySend(user?.let { u -> FirebaseUser(u) }) }
        awaitClose { unsub() }
    }
    
    actual fun addIdTokenListener(listener: IdTokenListener) {
        AuthModule.onIdTokenChanged(jsAuth) { listener.onIdTokenChanged(this) }
    }
    actual fun removeIdTokenListener(listener: IdTokenListener) {}
    
    actual val idTokenChanges: Flow<FirebaseUser?> = callbackFlow {
        val unsub = AuthModule.onIdTokenChanged(jsAuth) { user -> trySend(user?.let { u -> FirebaseUser(u) }) }
        awaitClose { unsub() }
    }
    
    actual suspend fun fetchSignInMethodsForEmail(email: String): SignInMethodQueryResult {
        val methods = AuthModule.fetchSignInMethodsForEmail(jsAuth, email).await<dynamic>() as Array<String>
        return SignInMethodQueryResult(methods.toList())
    }
    
    actual suspend fun updateCurrentUser(user: FirebaseUser) { AuthModule.updateCurrentUser(jsAuth, user.jsUser).await<dynamic>() }
    actual fun useEmulator(host: String, port: Int) { AuthModule.connectAuthEmulator(jsAuth, "http://$host:$port") }
}

actual interface AuthStateListener { actual fun onAuthStateChanged(auth: FirebaseAuth) }
actual interface IdTokenListener { actual fun onIdTokenChanged(auth: FirebaseAuth) }

actual class FirebaseUser internal constructor(internal val jsUser: dynamic) {
    actual val uid: String get() = jsUser.uid as String
    actual val email: String? get() = jsUser.email as? String
    actual val displayName: String? get() = jsUser.displayName as? String
    actual val phoneNumber: String? get() = jsUser.phoneNumber as? String
    actual val photoUrl: String? get() = jsUser.photoURL as? String
    actual val isAnonymous: Boolean get() = jsUser.isAnonymous as Boolean
    actual val isEmailVerified: Boolean get() = jsUser.emailVerified as Boolean
    actual val providerId: String get() = jsUser.providerId as String
    actual val metadata: UserMetadata? get() = jsUser.metadata?.let { m -> UserMetadata(m) }
    actual val providerData: List<UserInfo> get() = (jsUser.providerData as Array<dynamic>).map { d -> UserInfo(d) }
    
    actual suspend fun getIdToken(forceRefresh: Boolean): GetTokenResult {
        val result = AuthModule.getIdTokenResult(jsUser, forceRefresh).await<dynamic>()
        return GetTokenResult(result)
    }
    actual suspend fun reload() { AuthModule.reload(jsUser).await<dynamic>() }
    actual suspend fun delete() { AuthModule.deleteUser(jsUser).await<dynamic>() }
    actual suspend fun sendEmailVerification() { AuthModule.sendEmailVerification(jsUser).await<dynamic>() }
    actual suspend fun updateEmail(email: String) { AuthModule.updateEmail(jsUser, email).await<dynamic>() }
    actual suspend fun updatePassword(password: String) { AuthModule.updatePassword(jsUser, password).await<dynamic>() }
    
    actual suspend fun updateProfile(request: UserProfileChangeRequest) {
        val profile = js("{}")
        request.displayName?.let { profile.displayName = it }
        request.photoUri?.let { profile.photoURL = it }
        AuthModule.updateProfile(jsUser, profile).await<dynamic>()
    }
    
    actual suspend fun linkWithCredential(credential: AuthCredential): AuthResult {
        val result = AuthModule.linkWithCredential(jsUser, credential.jsCredential).await<dynamic>()
        return AuthResult(result)
    }
    
    actual suspend fun unlink(providerId: String): FirebaseUser {
        val result = AuthModule.unlink(jsUser, providerId).await<dynamic>()
        return FirebaseUser(result)
    }
    
    actual suspend fun reauthenticate(credential: AuthCredential) {
        AuthModule.reauthenticateWithCredential(jsUser, credential.jsCredential).await<dynamic>()
    }
}

actual class UserMetadata internal constructor(private val jsMeta: dynamic) {
    actual val creationTimestamp: Long? get() = (jsMeta.creationTime as? String)?.let { Date.parse(it).toLong() }
    actual val lastSignInTimestamp: Long? get() = (jsMeta.lastSignInTime as? String)?.let { Date.parse(it).toLong() }
}

actual class UserInfo internal constructor(private val jsInfo: dynamic) {
    actual val providerId: String get() = jsInfo.providerId as String
    actual val uid: String get() = jsInfo.uid as String
    actual val displayName: String? get() = jsInfo.displayName as? String
    actual val email: String? get() = jsInfo.email as? String
    actual val phoneNumber: String? get() = jsInfo.phoneNumber as? String
    actual val photoUrl: String? get() = jsInfo.photoURL as? String
}

actual class GetTokenResult internal constructor(private val jsResult: dynamic) {
    actual val token: String? get() = jsResult.token as? String
    actual val expirationTimestamp: Long get() = Date.parse(jsResult.expirationTime as String).toLong()
    actual val claims: Map<String, Any> get() = jsResult.claims.unsafeCast<Map<String, Any>>()
}

actual class UserProfileChangeRequest internal constructor(val displayName: String? = null, val photoUri: String? = null) {
    actual class Builder {
        private var displayName: String? = null
        private var photoUri: String? = null
        actual fun setDisplayName(displayName: String?): Builder { this.displayName = displayName; return this }
        actual fun setPhotoUri(photoUri: String?): Builder { this.photoUri = photoUri; return this }
        actual fun build(): UserProfileChangeRequest = UserProfileChangeRequest(displayName, photoUri)
    }
}

actual class AuthResult internal constructor(private val jsResult: dynamic) {
    actual val user: FirebaseUser? get() = jsResult.user?.let { u -> FirebaseUser(u) }
    actual val additionalUserInfo: AdditionalUserInfo? get() = jsResult._tokenResponse?.let { t -> AdditionalUserInfo(t) }
    actual val credential: AuthCredential? get() = null
}

actual class AdditionalUserInfo internal constructor(private val jsInfo: dynamic) {
    actual val providerId: String? get() = jsInfo.providerId as? String
    actual val username: String? get() = jsInfo.screenName as? String
    actual val profile: Map<String, Any>? get() = null
    actual val isNewUser: Boolean get() = jsInfo.isNewUser as? Boolean ?: false
}

actual open class AuthCredential internal constructor(internal val jsCredential: dynamic) {
    actual val provider: String get() = jsCredential.providerId as String
    actual val signInMethod: String get() = jsCredential.signInMethod as String
}

actual class ActionCodeResult internal constructor(private val jsResult: dynamic? = null) {
    actual val operation: Int get() = when (jsResult?.operation as? String) {
        "PASSWORD_RESET" -> PASSWORD_RESET
        "VERIFY_EMAIL" -> VERIFY_EMAIL
        "RECOVER_EMAIL" -> RECOVER_EMAIL
        "EMAIL_SIGNIN" -> EMAIL_SIGNIN
        "VERIFY_AND_CHANGE_EMAIL" -> VERIFY_BEFORE_CHANGE_EMAIL
        "REVERT_SECOND_FACTOR_ADDITION" -> REVERT_SECOND_FACTOR_ADDITION
        else -> -1
    }
    actual val email: String? get() = jsResult?.data?.email as? String
    actual val previousEmail: String? get() = jsResult?.data?.previousEmail as? String
    
    actual companion object {
        actual val PASSWORD_RESET: Int = 0
        actual val VERIFY_EMAIL: Int = 1
        actual val RECOVER_EMAIL: Int = 2
        actual val EMAIL_SIGNIN: Int = 3
        actual val VERIFY_BEFORE_CHANGE_EMAIL: Int = 4
        actual val REVERT_SECOND_FACTOR_ADDITION: Int = 5
    }
}

actual class SignInMethodQueryResult internal constructor(private val methods: List<String>) {
    actual val signInMethods: List<String> get() = methods
}

// Auth Providers
actual object EmailAuthProvider {
    actual val PROVIDER_ID: String = "password"
    actual val EMAIL_PASSWORD_SIGN_IN_METHOD: String = "password"
    actual val EMAIL_LINK_SIGN_IN_METHOD: String = "emailLink"
    actual fun getCredential(email: String, password: String): AuthCredential = AuthCredential(AuthModule.EmailAuthProvider.credential(email, password))
    actual fun getCredentialWithLink(email: String, emailLink: String): AuthCredential = AuthCredential(AuthModule.EmailAuthProvider.credentialWithLink(email, emailLink))
}

actual object GoogleAuthProvider {
    actual val PROVIDER_ID: String = "google.com"
    actual val GOOGLE_SIGN_IN_METHOD: String = "google.com"
    actual fun getCredential(idToken: String?, accessToken: String?): AuthCredential = AuthCredential(AuthModule.GoogleAuthProvider.credential(idToken, accessToken))
}

actual object FacebookAuthProvider {
    actual val PROVIDER_ID: String = "facebook.com"
    actual val FACEBOOK_SIGN_IN_METHOD: String = "facebook.com"
    actual fun getCredential(accessToken: String): AuthCredential = AuthCredential(AuthModule.FacebookAuthProvider.credential(accessToken))
}

actual class PhoneAuthProvider internal constructor(private val jsProvider: dynamic) {
    actual companion object {
        actual val PROVIDER_ID: String = "phone"
        actual val PHONE_SIGN_IN_METHOD: String = "phone"
        actual fun getInstance(): PhoneAuthProvider = PhoneAuthProvider(null)
        actual fun getInstance(auth: FirebaseAuth): PhoneAuthProvider = PhoneAuthProvider(null)
        actual fun getCredential(verificationId: String, smsCode: String): PhoneAuthCredential = PhoneAuthCredential(AuthModule.PhoneAuthProvider.credential(verificationId, smsCode))
    }
    actual fun verifyPhoneNumber(options: PhoneAuthOptions) {}
}

actual class PhoneAuthCredential internal constructor(jsCredential: dynamic) : AuthCredential(jsCredential) {
    actual val smsCode: String? get() = null
}

actual class PhoneAuthOptions internal constructor() {
    actual class Builder actual constructor(auth: FirebaseAuth) {
        actual fun setPhoneNumber(phoneNumber: String): Builder = this
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder = this
        actual fun setCallbacks(callbacks: PhoneAuthCallbacks): Builder = this
        actual fun build(): PhoneAuthOptions = PhoneAuthOptions()
    }
}

actual enum class TimeUnit { SECONDS, MILLISECONDS, MINUTES }

actual interface PhoneAuthCallbacks {
    actual fun onVerificationCompleted(credential: PhoneAuthCredential)
    actual fun onVerificationFailed(exception: FirebaseAuthException)
    actual fun onCodeSent(verificationId: String, token: ForceResendingToken)
    actual fun onCodeAutoRetrievalTimeOut(verificationId: String)
}

actual class ForceResendingToken

actual class FirebaseAuthException actual constructor(
    actual override val message: String,
    actual val errorCode: String
) : Exception(message)

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}
