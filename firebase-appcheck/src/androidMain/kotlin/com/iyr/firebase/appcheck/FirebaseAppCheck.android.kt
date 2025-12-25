package com.iyr.firebase.appcheck

import com.google.firebase.appcheck.FirebaseAppCheck as AndroidAppCheck
import com.google.firebase.appcheck.AppCheckToken as AndroidToken
import com.google.firebase.appcheck.AppCheckProviderFactory as AndroidFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory as AndroidPlayIntegrity
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory as AndroidDebug
import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.tasks.await

actual class FirebaseAppCheck internal constructor(
    private val android: AndroidAppCheck
) {
    actual companion object {
        actual fun getInstance(): FirebaseAppCheck = FirebaseAppCheck(AndroidAppCheck.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseAppCheck = FirebaseAppCheck(AndroidAppCheck.getInstance(app.android))
    }
    
    actual fun installAppCheckProviderFactory(factory: AppCheckProviderFactory) {
        when (factory) {
            is PlayIntegrityAppCheckProviderFactory -> android.installAppCheckProviderFactory(factory.android)
            is DebugAppCheckProviderFactory -> android.installAppCheckProviderFactory(factory.android)
        }
    }
    
    actual suspend fun getToken(forceRefresh: Boolean): AppCheckToken {
        val result = android.getAppCheckToken(forceRefresh).await()
        return AppCheckToken(result.token, result.expireTimeMillis)
    }
    
    actual fun addAppCheckListener(listener: AppCheckListener) {
        android.addAppCheckListener { token -> listener.onAppCheckTokenChanged(AppCheckToken(token.token, token.expireTimeMillis)) }
    }
    
    actual fun removeAppCheckListener(listener: AppCheckListener) { /* Android no expone removeListener fácilmente */ }
    actual fun setTokenAutoRefreshEnabled(enabled: Boolean) { android.setTokenAutoRefreshEnabled(enabled) }
}

actual interface AppCheckProviderFactory
actual class AppCheckToken(actual val token: String, actual val expireTimeMillis: Long)
actual interface AppCheckListener { actual fun onAppCheckTokenChanged(token: AppCheckToken) }

actual class PlayIntegrityAppCheckProviderFactory internal constructor(
    internal val android: AndroidPlayIntegrity
) : AppCheckProviderFactory {
    actual companion object {
        actual fun getInstance(): PlayIntegrityAppCheckProviderFactory = PlayIntegrityAppCheckProviderFactory(AndroidPlayIntegrity.getInstance())
    }
}

actual class DebugAppCheckProviderFactory internal constructor(
    internal val android: AndroidDebug
) : AppCheckProviderFactory {
    actual companion object {
        actual fun getInstance(): DebugAppCheckProviderFactory = DebugAppCheckProviderFactory(AndroidDebug.getInstance())
    }
}

