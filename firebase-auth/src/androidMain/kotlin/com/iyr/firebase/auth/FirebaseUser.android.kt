package com.iyr.firebase.auth

import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseUser as AndroidUser

actual class FirebaseUser internal constructor(
    internal val android: AndroidUser
) {
    actual val uid: String get() = android.uid
    actual val email: String? get() = android.email
    actual val displayName: String? get() = android.displayName
    actual val phoneNumber: String? get() = android.phoneNumber
    actual val photoUrl: String? get() = android.photoUrl?.toString()
    actual val isAnonymous: Boolean get() = android.isAnonymous
    actual val isEmailVerified: Boolean get() = android.isEmailVerified
    actual val providerId: String get() = android.providerId
    actual val providerData: List<UserInfo> get() = android.providerData.map { UserInfo(it) }
    actual val metadata: UserMetadata? get() = android.metadata?.let { UserMetadata(it) }
    
    actual suspend fun getIdToken(forceRefresh: Boolean): GetTokenResult {
        return GetTokenResult(android.getIdToken(forceRefresh).await())
    }
    
    actual suspend fun reload() { android.reload().await() }
    actual suspend fun delete() { android.delete().await() }
    actual suspend fun sendEmailVerification() { android.sendEmailVerification().await() }
    
    @Suppress("DEPRECATION")
    actual suspend fun updateEmail(email: String) { android.updateEmail(email).await() }
    actual suspend fun updatePassword(password: String) { android.updatePassword(password).await() }
    
    actual suspend fun updateProfile(request: UserProfileChangeRequest) {
        android.updateProfile(request.android).await()
    }
    
    actual suspend fun linkWithCredential(credential: AuthCredential): AuthResult {
        return AuthResult(android.linkWithCredential(credential.android).await())
    }
    
    actual suspend fun unlink(providerId: String): FirebaseUser {
        return FirebaseUser(android.unlink(providerId).await().user!!)
    }
    
    actual suspend fun reauthenticate(credential: AuthCredential) {
        android.reauthenticate(credential.android).await()
    }
}

actual class UserInfo internal constructor(private val android: com.google.firebase.auth.UserInfo) {
    actual val providerId: String get() = android.providerId
    actual val uid: String get() = android.uid
    actual val displayName: String? get() = android.displayName
    actual val email: String? get() = android.email
    actual val phoneNumber: String? get() = android.phoneNumber
    actual val photoUrl: String? get() = android.photoUrl?.toString()
}

actual class UserMetadata internal constructor(private val android: com.google.firebase.auth.FirebaseUserMetadata) {
    actual val creationTimestamp: Long? get() = android.creationTimestamp
    actual val lastSignInTimestamp: Long? get() = android.lastSignInTimestamp
}

actual class GetTokenResult internal constructor(private val android: com.google.firebase.auth.GetTokenResult) {
    actual val token: String? get() = android.token
    actual val expirationTimestamp: Long get() = android.expirationTimestamp
    actual val claims: Map<String, Any> get() = android.claims
}

actual class UserProfileChangeRequest internal constructor(internal val android: com.google.firebase.auth.UserProfileChangeRequest) {
    actual class Builder {
        private val builder = com.google.firebase.auth.UserProfileChangeRequest.Builder()
        actual fun setDisplayName(displayName: String?): Builder { builder.setDisplayName(displayName); return this }
        actual fun setPhotoUri(photoUri: String?): Builder { 
            builder.setPhotoUri(photoUri?.let { android.net.Uri.parse(it) })
            return this 
        }
        actual fun build(): UserProfileChangeRequest = UserProfileChangeRequest(builder.build())
    }
}






