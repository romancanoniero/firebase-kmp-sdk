package com.iyr.firebase.auth

actual open class AuthCredential internal constructor(val js: dynamic) {
    actual val provider: String get() = js.providerId as String
    actual val signInMethod: String get() = js.signInMethod as String
}

actual class AuthResult internal constructor(val js: dynamic) {
    actual val user: FirebaseUser? get() = js.user?.let { FirebaseUser(it) }
    actual val additionalUserInfo: AdditionalUserInfo? get() = null
    actual val credential: AuthCredential? get() = null
}

actual class AdditionalUserInfo internal constructor(val js: dynamic) {
    actual val providerId: String? get() = null
    actual val username: String? get() = null
    actual val profile: Map<String, Any>? get() = null
    actual val isNewUser: Boolean get() = false
}

actual class ActionCodeResult {
    actual val operation: Int get() = 0
    actual val email: String? get() = null
    actual val previousEmail: String? get() = null
    actual companion object {
        actual val PASSWORD_RESET: Int = 0
        actual val VERIFY_EMAIL: Int = 1
        actual val RECOVER_EMAIL: Int = 2
        actual val EMAIL_SIGNIN: Int = 3
        actual val VERIFY_BEFORE_CHANGE_EMAIL: Int = 4
        actual val REVERT_SECOND_FACTOR_ADDITION: Int = 5
    }
}

actual class SignInMethodQueryResult { actual val signInMethods: List<String> get() = emptyList() }
