package com.iyr.firebase.auth

import cocoapods.FirebaseAuth.*

actual open class AuthCredential internal constructor(
    internal val ios: FIRAuthCredential
) {
    actual val provider: String get() = ios.provider
    actual val signInMethod: String get() = ios.provider // iOS doesn't distinguish
}

actual class AuthResult internal constructor(
    private val ios: FIRAuthDataResult
) {
    actual val user: FirebaseUser? get() = ios.user?.let { FirebaseUser(it) }
    actual val additionalUserInfo: AdditionalUserInfo? get() = ios.additionalUserInfo?.let { AdditionalUserInfo(it) }
    actual val credential: AuthCredential? get() = ios.credential?.let { AuthCredential(it) }
}

actual class AdditionalUserInfo internal constructor(
    private val ios: FIRAdditionalUserInfo
) {
    actual val providerId: String? get() = ios.providerID
    actual val username: String? get() = ios.username
    @Suppress("UNCHECKED_CAST")
    actual val profile: Map<String, Any>? get() = ios.profile as? Map<String, Any>
    actual val isNewUser: Boolean get() = ios.newUser
}

actual class ActionCodeResult internal constructor(
    private val ios: FIRActionCodeInfo
) {
    actual val operation: Int get() = ios.operation.toInt()
    actual val email: String? get() = ios.email
    actual val previousEmail: String? get() = ios.previousEmail
    
    actual companion object {
        actual val PASSWORD_RESET: Int = FIRActionCodeOperationPasswordReset.toInt()
        actual val VERIFY_EMAIL: Int = FIRActionCodeOperationVerifyEmail.toInt()
        actual val RECOVER_EMAIL: Int = FIRActionCodeOperationRecoverEmail.toInt()
        actual val EMAIL_SIGNIN: Int = FIRActionCodeOperationEmailLink.toInt()
        actual val VERIFY_BEFORE_CHANGE_EMAIL: Int = FIRActionCodeOperationVerifyAndChangeEmail.toInt()
        actual val REVERT_SECOND_FACTOR_ADDITION: Int = FIRActionCodeOperationRevertSecondFactorAddition.toInt()
    }
}

actual class SignInMethodQueryResult internal constructor(
    private val methods: List<*>?
) {
    @Suppress("UNCHECKED_CAST")
    actual val signInMethods: List<String> get() = (methods as? List<String>) ?: emptyList()
}






