package com.iyr.firebase.auth

/**
 * EmailAuthProvider - Proveedor de autenticación por email/password
 */
expect object EmailAuthProvider {
    val PROVIDER_ID: String
    val EMAIL_PASSWORD_SIGN_IN_METHOD: String
    val EMAIL_LINK_SIGN_IN_METHOD: String
    
    fun getCredential(email: String, password: String): AuthCredential
    fun getCredentialWithLink(email: String, emailLink: String): AuthCredential
}

/**
 * GoogleAuthProvider - Proveedor de autenticación de Google
 */
expect object GoogleAuthProvider {
    val PROVIDER_ID: String
    val GOOGLE_SIGN_IN_METHOD: String
    
    fun getCredential(idToken: String?, accessToken: String?): AuthCredential
}

/**
 * FacebookAuthProvider - Proveedor de autenticación de Facebook
 */
expect object FacebookAuthProvider {
    val PROVIDER_ID: String
    val FACEBOOK_SIGN_IN_METHOD: String
    
    fun getCredential(accessToken: String): AuthCredential
}

/**
 * PhoneAuthProvider - Proveedor de autenticación por teléfono
 */
expect class PhoneAuthProvider {
    companion object {
        val PROVIDER_ID: String
        val PHONE_SIGN_IN_METHOD: String
        
        fun getInstance(): PhoneAuthProvider
        fun getInstance(auth: FirebaseAuth): PhoneAuthProvider
        fun getCredential(verificationId: String, smsCode: String): PhoneAuthCredential
    }
    
    fun verifyPhoneNumber(options: PhoneAuthOptions)
}

expect class PhoneAuthCredential : AuthCredential {
    val smsCode: String?
}

expect class PhoneAuthOptions {
    class Builder(auth: FirebaseAuth) {
        fun setPhoneNumber(phoneNumber: String): Builder
        fun setTimeout(timeout: Long, unit: TimeUnit): Builder
        fun setCallbacks(callbacks: PhoneAuthCallbacks): Builder
        fun build(): PhoneAuthOptions
    }
}

expect enum class TimeUnit {
    SECONDS, MILLISECONDS, MINUTES
}

expect interface PhoneAuthCallbacks {
    fun onVerificationCompleted(credential: PhoneAuthCredential)
    fun onVerificationFailed(exception: FirebaseAuthException)
    fun onCodeSent(verificationId: String, token: ForceResendingToken)
    fun onCodeAutoRetrievalTimeOut(verificationId: String)
}

expect class ForceResendingToken

expect class FirebaseAuthException(message: String, errorCode: String) : Exception {
    val errorCode: String
    override val message: String
}

