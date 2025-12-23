package com.iyr.firebase.auth

actual class FirebaseUser internal constructor(val js: dynamic) {
    actual val uid: String get() = js.uid as String
    actual val email: String? get() = js.email as? String
    actual val displayName: String? get() = js.displayName as? String
    actual val phoneNumber: String? get() = js.phoneNumber as? String
    actual val photoUrl: String? get() = js.photoURL as? String
    actual val isAnonymous: Boolean get() = js.isAnonymous as Boolean
    actual val isEmailVerified: Boolean get() = js.emailVerified as Boolean
    actual val providerId: String get() = js.providerId as String
    actual val providerData: List<UserInfo> get() = emptyList()
    actual val metadata: UserMetadata? get() = null
    actual suspend fun getIdToken(forceRefresh: Boolean): GetTokenResult = TODO()
    actual suspend fun reload() {}
    actual suspend fun delete() {}
    actual suspend fun sendEmailVerification() {}
    actual suspend fun updateEmail(email: String) {}
    actual suspend fun updatePassword(password: String) {}
    actual suspend fun updateProfile(request: UserProfileChangeRequest) {}
    actual suspend fun linkWithCredential(credential: AuthCredential): AuthResult = TODO()
    actual suspend fun unlink(providerId: String): FirebaseUser = TODO()
    actual suspend fun reauthenticate(credential: AuthCredential) {}
}

actual class UserInfo internal constructor(val js: dynamic) {
    actual val providerId: String get() = js.providerId as String
    actual val uid: String get() = js.uid as String
    actual val displayName: String? get() = js.displayName as? String
    actual val email: String? get() = js.email as? String
    actual val phoneNumber: String? get() = js.phoneNumber as? String
    actual val photoUrl: String? get() = js.photoURL as? String
}

actual class UserMetadata { actual val creationTimestamp: Long? get() = null; actual val lastSignInTimestamp: Long? get() = null }
actual class GetTokenResult { actual val token: String? get() = null; actual val expirationTimestamp: Long get() = 0; actual val claims: Map<String, Any> get() = emptyMap() }
actual class UserProfileChangeRequest(val displayName: String?, val photoUri: String?) {
    actual class Builder {
        private var dn: String? = null; private var pu: String? = null
        actual fun setDisplayName(displayName: String?): Builder { dn = displayName; return this }
        actual fun setPhotoUri(photoUri: String?): Builder { pu = photoUri; return this }
        actual fun build(): UserProfileChangeRequest = UserProfileChangeRequest(dn, pu)
    }
}
