package com.iyr.firebase.auth

import com.google.firebase.auth.PhoneAuthProvider as AndroidPhoneAuthProvider
import java.util.concurrent.TimeUnit as JavaTimeUnit

actual object EmailAuthProvider {
    actual val PROVIDER_ID: String = com.google.firebase.auth.EmailAuthProvider.PROVIDER_ID
    actual val EMAIL_PASSWORD_SIGN_IN_METHOD: String = com.google.firebase.auth.EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD
    actual val EMAIL_LINK_SIGN_IN_METHOD: String = com.google.firebase.auth.EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD
    
    actual fun getCredential(email: String, password: String): AuthCredential {
        return AuthCredential(com.google.firebase.auth.EmailAuthProvider.getCredential(email, password))
    }
    
    actual fun getCredentialWithLink(email: String, emailLink: String): AuthCredential {
        return AuthCredential(com.google.firebase.auth.EmailAuthProvider.getCredentialWithLink(email, emailLink))
    }
}

actual object GoogleAuthProvider {
    actual val PROVIDER_ID: String = com.google.firebase.auth.GoogleAuthProvider.PROVIDER_ID
    actual val GOOGLE_SIGN_IN_METHOD: String = com.google.firebase.auth.GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD
    
    actual fun getCredential(idToken: String?, accessToken: String?): AuthCredential {
        return AuthCredential(com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, accessToken))
    }
}

actual object FacebookAuthProvider {
    actual val PROVIDER_ID: String = com.google.firebase.auth.FacebookAuthProvider.PROVIDER_ID
    actual val FACEBOOK_SIGN_IN_METHOD: String = com.google.firebase.auth.FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD
    
    actual fun getCredential(accessToken: String): AuthCredential {
        return AuthCredential(com.google.firebase.auth.FacebookAuthProvider.getCredential(accessToken))
    }
}

actual class PhoneAuthProvider internal constructor(
    private val android: AndroidPhoneAuthProvider
) {
    actual companion object {
        actual val PROVIDER_ID: String = AndroidPhoneAuthProvider.PROVIDER_ID
        actual val PHONE_SIGN_IN_METHOD: String = AndroidPhoneAuthProvider.PHONE_SIGN_IN_METHOD
        
        actual fun getInstance(): PhoneAuthProvider = PhoneAuthProvider(AndroidPhoneAuthProvider.getInstance())
        actual fun getInstance(auth: FirebaseAuth): PhoneAuthProvider = PhoneAuthProvider(AndroidPhoneAuthProvider.getInstance(auth.android))
        
        actual fun getCredential(verificationId: String, smsCode: String): PhoneAuthCredential {
            return PhoneAuthCredential(AndroidPhoneAuthProvider.getCredential(verificationId, smsCode))
        }
    }
    
    actual fun verifyPhoneNumber(options: PhoneAuthOptions) {
        AndroidPhoneAuthProvider.verifyPhoneNumber(options.android)
    }
}

actual class PhoneAuthCredential internal constructor(
    private val androidCredential: com.google.firebase.auth.PhoneAuthCredential
) : AuthCredential(androidCredential) {
    actual val smsCode: String? get() = androidCredential.smsCode
}

actual class PhoneAuthOptions internal constructor(
    internal val android: com.google.firebase.auth.PhoneAuthOptions
) {
    actual class Builder actual constructor(auth: FirebaseAuth) {
        private val builder = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth.android)
        
        actual fun setPhoneNumber(phoneNumber: String): Builder { builder.setPhoneNumber(phoneNumber); return this }
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder { 
            builder.setTimeout(timeout, unit.toJava())
            return this 
        }
        actual fun setCallbacks(callbacks: PhoneAuthCallbacks): Builder { 
            builder.setCallbacks(callbacks.toAndroid())
            return this 
        }
        actual fun build(): PhoneAuthOptions = PhoneAuthOptions(builder.build())
    }
}

actual enum class TimeUnit {
    SECONDS, MILLISECONDS, MINUTES;
    
    fun toJava(): JavaTimeUnit = when(this) {
        SECONDS -> JavaTimeUnit.SECONDS
        MILLISECONDS -> JavaTimeUnit.MILLISECONDS
        MINUTES -> JavaTimeUnit.MINUTES
    }
}

actual interface PhoneAuthCallbacks {
    actual fun onVerificationCompleted(credential: PhoneAuthCredential)
    actual fun onVerificationFailed(exception: FirebaseAuthException)
    actual fun onCodeSent(verificationId: String, token: ForceResendingToken)
    actual fun onCodeAutoRetrievalTimeOut(verificationId: String)
}

private fun PhoneAuthCallbacks.toAndroid() = object : AndroidPhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
        this@toAndroid.onVerificationCompleted(PhoneAuthCredential(credential))
    }
    override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
        this@toAndroid.onVerificationFailed(FirebaseAuthException(e.message ?: "Unknown error", ""))
    }
    override fun onCodeSent(verificationId: String, token: AndroidPhoneAuthProvider.ForceResendingToken) {
        this@toAndroid.onCodeSent(verificationId, ForceResendingToken(token))
    }
    override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
        this@toAndroid.onCodeAutoRetrievalTimeOut(verificationId)
    }
}

actual class ForceResendingToken internal constructor(
    internal val android: AndroidPhoneAuthProvider.ForceResendingToken
)

actual class FirebaseAuthException actual constructor(
    message: String,
    actual val errorCode: String
) : Exception(message) {
    private val _message = message
    actual override val message: String get() = _message
}

