package com.iyr.firebase.auth

/**
 * AuthCredential - Credencial de autenticación
 */
expect open class AuthCredential {
    val provider: String
    val signInMethod: String
}

/**
 * AuthResult - Resultado de operación de autenticación
 */
expect class AuthResult {
    val user: FirebaseUser?
    val additionalUserInfo: AdditionalUserInfo?
    val credential: AuthCredential?
}

expect class AdditionalUserInfo {
    val providerId: String?
    val username: String?
    val profile: Map<String, Any>?
    val isNewUser: Boolean
}

expect class ActionCodeResult {
    val operation: Int
    val email: String?
    val previousEmail: String?
    
    companion object {
        val PASSWORD_RESET: Int
        val VERIFY_EMAIL: Int
        val RECOVER_EMAIL: Int
        val EMAIL_SIGNIN: Int
        val VERIFY_BEFORE_CHANGE_EMAIL: Int
        val REVERT_SECOND_FACTOR_ADDITION: Int
    }
}

expect class SignInMethodQueryResult {
    val signInMethods: List<String>
}






