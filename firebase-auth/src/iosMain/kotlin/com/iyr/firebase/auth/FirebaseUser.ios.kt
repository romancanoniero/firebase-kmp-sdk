package com.iyr.firebase.auth

import cocoapods.FirebaseAuth.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseUser internal constructor(
    internal val ios: FIRUser
) {
    actual val uid: String get() = ios.uid
    actual val email: String? get() = ios.email
    actual val displayName: String? get() = ios.displayName
    actual val phoneNumber: String? get() = ios.phoneNumber
    actual val photoUrl: String? get() = ios.photoURL?.absoluteString
    actual val isAnonymous: Boolean get() = ios.anonymous
    actual val isEmailVerified: Boolean get() = ios.emailVerified
    actual val providerId: String get() = ios.providerID
    actual val providerData: List<UserInfo> get() = ios.providerData.mapNotNull { 
        (it as? FIRUserInfoProtocol)?.let { UserInfo(it) }
    }
    actual val metadata: UserMetadata? get() = ios.metadata?.let { UserMetadata(it) }
    
    actual suspend fun getIdToken(forceRefresh: Boolean): GetTokenResult =
        suspendCancellableCoroutine { cont ->
            ios.getIDTokenResultForcingRefresh(forceRefresh) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(GetTokenResult(result!!))
            }
        }
    
    actual suspend fun reload(): Unit = suspendCancellableCoroutine { cont ->
        ios.reloadWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun delete(): Unit = suspendCancellableCoroutine { cont ->
        ios.deleteWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun sendEmailVerification(): Unit = suspendCancellableCoroutine { cont ->
        ios.sendEmailVerificationWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun updateEmail(email: String): Unit = suspendCancellableCoroutine { cont ->
        ios.updateEmail(email) { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun updatePassword(password: String): Unit = suspendCancellableCoroutine { cont ->
        ios.updatePassword(password) { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun updateProfile(request: UserProfileChangeRequest): Unit = 
        suspendCancellableCoroutine { cont ->
            val changeRequest = ios.profileChangeRequest()
            request.displayName?.let { changeRequest.displayName = it }
            request.photoUri?.let { changeRequest.photoURL = platform.Foundation.NSURL(string = it) }
            changeRequest.commitChangesWithCompletion { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    
    actual suspend fun linkWithCredential(credential: AuthCredential): AuthResult =
        suspendCancellableCoroutine { cont ->
            ios.linkWithCredential(credential.ios) { result, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(AuthResult(result!!))
            }
        }
    
    actual suspend fun unlink(providerId: String): FirebaseUser =
        suspendCancellableCoroutine { cont ->
            ios.unlinkFromProvider(providerId) { user, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(FirebaseUser(user!!))
            }
        }
    
    actual suspend fun reauthenticate(credential: AuthCredential): Unit =
        suspendCancellableCoroutine { cont ->
            ios.reauthenticateWithCredential(credential.ios) { _, error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
}

actual class UserInfo internal constructor(private val ios: FIRUserInfoProtocol) {
    actual val providerId: String get() = ios.providerID
    actual val uid: String get() = ios.uid
    actual val displayName: String? get() = ios.displayName
    actual val email: String? get() = ios.email
    actual val phoneNumber: String? get() = ios.phoneNumber
    actual val photoUrl: String? get() = ios.photoURL?.absoluteString
}

actual class UserMetadata internal constructor(private val ios: FIRUserMetadata) {
    actual val creationTimestamp: Long? get() = ios.creationDate?.timeIntervalSince1970?.toLong()?.times(1000)
    actual val lastSignInTimestamp: Long? get() = ios.lastSignInDate?.timeIntervalSince1970?.toLong()?.times(1000)
}

actual class GetTokenResult internal constructor(private val ios: FIRAuthTokenResult) {
    actual val token: String? get() = ios.token
    actual val expirationTimestamp: Long get() = (ios.expirationDate?.timeIntervalSince1970?.toLong() ?: 0) * 1000
    @Suppress("UNCHECKED_CAST")
    actual val claims: Map<String, Any> get() = ios.claims as? Map<String, Any> ?: emptyMap()
}

actual class UserProfileChangeRequest internal constructor(
    internal val displayName: String?,
    internal val photoUri: String?
) {
    actual class Builder {
        private var displayName: String? = null
        private var photoUri: String? = null
        
        actual fun setDisplayName(displayName: String?): Builder { this.displayName = displayName; return this }
        actual fun setPhotoUri(photoUri: String?): Builder { this.photoUri = photoUri; return this }
        actual fun build(): UserProfileChangeRequest = UserProfileChangeRequest(displayName, photoUri)
    }
}

private fun NSError.toException() = FirebaseAuthException(
    message = localizedDescription,
    errorCode = code.toString()
)

