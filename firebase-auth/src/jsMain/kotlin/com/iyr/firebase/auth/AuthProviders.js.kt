package com.iyr.firebase.auth

actual object EmailAuthProvider {
    actual val PROVIDER_ID: String = "password"
    actual val EMAIL_PASSWORD_SIGN_IN_METHOD: String = "password"
    actual val EMAIL_LINK_SIGN_IN_METHOD: String = "emailLink"
    actual fun getCredential(email: String, password: String): AuthCredential = AuthCredential()
    actual fun getCredentialWithLink(email: String, emailLink: String): AuthCredential = AuthCredential()
}

actual object GoogleAuthProvider {
    actual val PROVIDER_ID: String = "google.com"
    actual val GOOGLE_SIGN_IN_METHOD: String = "google.com"
    actual fun getCredential(idToken: String?, accessToken: String?): AuthCredential = AuthCredential()
}

actual object FacebookAuthProvider {
    actual val PROVIDER_ID: String = "facebook.com"
    actual val FACEBOOK_SIGN_IN_METHOD: String = "facebook.com"
    actual fun getCredential(accessToken: String): AuthCredential = AuthCredential()
}

actual class PhoneAuthProvider {
    actual companion object {
        actual val PROVIDER_ID: String = "phone"
        actual val PHONE_SIGN_IN_METHOD: String = "phone"
        actual fun getInstance(): PhoneAuthProvider = PhoneAuthProvider()
        actual fun getInstance(auth: FirebaseAuth): PhoneAuthProvider = PhoneAuthProvider()
        actual fun getCredential(verificationId: String, smsCode: String): PhoneAuthCredential = PhoneAuthCredential()
    }
    actual fun verifyPhoneNumber(options: PhoneAuthOptions) {}
}

actual class PhoneAuthCredential : AuthCredential() { 
    actual val smsCode: String? get() = null 
}

actual class PhoneAuthOptions(val phoneNumber: String, val callbacks: PhoneAuthCallbacks) {
    actual class Builder actual constructor(auth: FirebaseAuth) {
        private var pn = ""; private var cb: PhoneAuthCallbacks? = null
        actual fun setPhoneNumber(phoneNumber: String): Builder { pn = phoneNumber; return this }
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder = this
        actual fun setCallbacks(callbacks: PhoneAuthCallbacks): Builder { cb = callbacks; return this }
        actual fun build(): PhoneAuthOptions = PhoneAuthOptions(pn, cb!!)
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

actual class FirebaseAuthException actual constructor(message: String, actual val errorCode: String) : Exception(message) {
    private val _msg = message
    actual override val message: String get() = _msg
}
