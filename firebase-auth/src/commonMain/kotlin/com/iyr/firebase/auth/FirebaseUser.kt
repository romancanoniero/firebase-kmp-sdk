package com.iyr.firebase.auth

/**
 * FirebaseUser - Usuario autenticado en Firebase
 */
expect class FirebaseUser {
    val uid: String
    val email: String?
    val displayName: String?
    val phoneNumber: String?
    val photoUrl: String?
    val isAnonymous: Boolean
    val isEmailVerified: Boolean
    val providerId: String
    val providerData: List<UserInfo>
    val metadata: UserMetadata?
    
    suspend fun getIdToken(forceRefresh: Boolean): GetTokenResult
    suspend fun reload()
    suspend fun delete()
    suspend fun sendEmailVerification()
    suspend fun updateEmail(email: String)
    suspend fun updatePassword(password: String)
    suspend fun updateProfile(request: UserProfileChangeRequest)
    suspend fun linkWithCredential(credential: AuthCredential): AuthResult
    suspend fun unlink(providerId: String): FirebaseUser
    suspend fun reauthenticate(credential: AuthCredential)
}

expect class UserInfo {
    val providerId: String
    val uid: String
    val displayName: String?
    val email: String?
    val phoneNumber: String?
    val photoUrl: String?
}

expect class UserMetadata {
    val creationTimestamp: Long?
    val lastSignInTimestamp: Long?
}

expect class GetTokenResult {
    val token: String?
    val expirationTimestamp: Long
    val claims: Map<String, Any>
}

expect class UserProfileChangeRequest {
    class Builder {
        fun setDisplayName(displayName: String?): Builder
        fun setPhotoUri(photoUri: String?): Builder
        fun build(): UserProfileChangeRequest
    }
}

