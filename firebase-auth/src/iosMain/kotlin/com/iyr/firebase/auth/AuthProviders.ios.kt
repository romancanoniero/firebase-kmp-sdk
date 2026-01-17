package com.iyr.firebase.auth

import cocoapods.FirebaseAuth.*
import platform.Foundation.NSError

actual object EmailAuthProvider {
    actual val PROVIDER_ID: String = FIREmailAuthProviderID
    actual val EMAIL_PASSWORD_SIGN_IN_METHOD: String = FIREmailPasswordAuthSignInMethod
    actual val EMAIL_LINK_SIGN_IN_METHOD: String = FIREmailLinkAuthSignInMethod
    
    actual fun getCredential(email: String, password: String): AuthCredential =
        AuthCredential(FIREmailAuthProvider.credentialWithEmail(email, password = password))
    
    actual fun getCredentialWithLink(email: String, emailLink: String): AuthCredential =
        AuthCredential(FIREmailAuthProvider.credentialWithEmail(email, link = emailLink))
}

actual object GoogleAuthProvider {
    actual val PROVIDER_ID: String = FIRGoogleAuthProviderID
    actual val GOOGLE_SIGN_IN_METHOD: String = FIRGoogleAuthSignInMethod
    
    actual fun getCredential(idToken: String?, accessToken: String?): AuthCredential =
        AuthCredential(FIRGoogleAuthProvider.credentialWithIDToken(idToken ?: "", accessToken = accessToken ?: ""))
}

actual object FacebookAuthProvider {
    actual val PROVIDER_ID: String = FIRFacebookAuthProviderID
    actual val FACEBOOK_SIGN_IN_METHOD: String = FIRFacebookAuthSignInMethod
    
    actual fun getCredential(accessToken: String): AuthCredential =
        AuthCredential(FIRFacebookAuthProvider.credentialWithAccessToken(accessToken))
}

actual class PhoneAuthProvider internal constructor(
    private val ios: FIRPhoneAuthProvider
) {
    actual companion object {
        actual val PROVIDER_ID: String = FIRPhoneAuthProviderID
        actual val PHONE_SIGN_IN_METHOD: String = FIRPhoneAuthSignInMethod
        
        actual fun getInstance(): PhoneAuthProvider = PhoneAuthProvider(FIRPhoneAuthProvider.provider())
        actual fun getInstance(auth: FirebaseAuth): PhoneAuthProvider = 
            PhoneAuthProvider(FIRPhoneAuthProvider.providerWithAuth(auth.ios))
        
        actual fun getCredential(verificationId: String, smsCode: String): PhoneAuthCredential =
            PhoneAuthCredential(FIRPhoneAuthProvider.provider().credentialWithVerificationID(verificationId, verificationCode = smsCode))
    }
    
    actual fun verifyPhoneNumber(options: PhoneAuthOptions) {
        ios.verifyPhoneNumber(options.phoneNumber, UIDelegate = null) { verificationId, error ->
            if (error != null) {
                options.callbacks.onVerificationFailed(FirebaseAuthException(
                    error.localizedDescription, error.code.toString()
                ))
            } else if (verificationId != null) {
                options.callbacks.onCodeSent(verificationId, ForceResendingToken())
            }
        }
    }
}

actual class PhoneAuthCredential internal constructor(
    private val iosCredential: FIRPhoneAuthCredential
) : AuthCredential(iosCredential) {
    actual val smsCode: String? get() = null // Not exposed in iOS SDK
}

actual class PhoneAuthOptions internal constructor(
    internal val phoneNumber: String,
    internal val callbacks: PhoneAuthCallbacks
) {
    actual class Builder actual constructor(auth: FirebaseAuth) {
        private var phoneNumber: String = ""
        private var callbacks: PhoneAuthCallbacks? = null
        
        actual fun setPhoneNumber(phoneNumber: String): Builder { this.phoneNumber = phoneNumber; return this }
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder = this // iOS handles timeout internally
        actual fun setCallbacks(callbacks: PhoneAuthCallbacks): Builder { this.callbacks = callbacks; return this }
        actual fun build(): PhoneAuthOptions = PhoneAuthOptions(phoneNumber, callbacks!!)
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

actual class FirebaseAuthException actual constructor(
    message: String,
    actual val errorCode: String
) : Exception(message) {
    private val _message = message
    actual override val message: String get() = _message
}






