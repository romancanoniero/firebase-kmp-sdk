package com.iyr.firebase.auth

import com.google.firebase.auth.AuthCredential as AndroidCredential

actual open class AuthCredential internal constructor(
    internal val android: AndroidCredential
) {
    actual val provider: String get() = android.provider
    actual val signInMethod: String get() = android.signInMethod
}

actual class AuthResult internal constructor(
    private val android: com.google.firebase.auth.AuthResult
) {
    actual val user: FirebaseUser? get() = android.user?.let { FirebaseUser(it) }
    actual val additionalUserInfo: AdditionalUserInfo? get() = android.additionalUserInfo?.let { AdditionalUserInfo(it) }
    actual val credential: AuthCredential? get() = android.credential?.let { AuthCredential(it) }
}

actual class AdditionalUserInfo internal constructor(
    private val android: com.google.firebase.auth.AdditionalUserInfo
) {
    actual val providerId: String? get() = android.providerId
    actual val username: String? get() = android.username
    actual val profile: Map<String, Any>? get() = android.profile
    actual val isNewUser: Boolean get() = android.isNewUser
}

actual class ActionCodeResult internal constructor(
    private val android: com.google.firebase.auth.ActionCodeResult
) {
    actual val operation: Int get() = android.operation
    actual val email: String? get() = android.info?.email
    actual val previousEmail: String? get() = null // Not available in Android SDK directly
    
    actual companion object {
        actual val PASSWORD_RESET: Int = com.google.firebase.auth.ActionCodeResult.PASSWORD_RESET
        actual val VERIFY_EMAIL: Int = com.google.firebase.auth.ActionCodeResult.VERIFY_EMAIL
        actual val RECOVER_EMAIL: Int = com.google.firebase.auth.ActionCodeResult.RECOVER_EMAIL
        actual val EMAIL_SIGNIN: Int = com.google.firebase.auth.ActionCodeResult.SIGN_IN_WITH_EMAIL_LINK
        actual val VERIFY_BEFORE_CHANGE_EMAIL: Int = com.google.firebase.auth.ActionCodeResult.VERIFY_BEFORE_CHANGE_EMAIL
        actual val REVERT_SECOND_FACTOR_ADDITION: Int = com.google.firebase.auth.ActionCodeResult.REVERT_SECOND_FACTOR_ADDITION
    }
}

actual class SignInMethodQueryResult internal constructor(
    private val android: com.google.firebase.auth.SignInMethodQueryResult
) {
    actual val signInMethods: List<String> get() = android.signInMethods ?: emptyList()
}

