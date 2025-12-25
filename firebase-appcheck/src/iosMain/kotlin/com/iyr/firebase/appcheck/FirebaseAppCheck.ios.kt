package com.iyr.firebase.appcheck

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseAppCheck.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseAppCheck internal constructor(
    private val ios: FIRAppCheck
) {
    actual companion object {
        actual fun getInstance(): FirebaseAppCheck = FirebaseAppCheck(FIRAppCheck.appCheck()!!)
        actual fun getInstance(app: FirebaseApp): FirebaseAppCheck {
            // iOS no soporta directamente getInstance(app) via cinterop
            return getInstance()
        }
    }
    
    actual fun installAppCheckProviderFactory(factory: AppCheckProviderFactory) {
        // iOS usa setAppCheckProviderFactory en la configuración inicial
    }
    
    actual suspend fun getToken(forceRefresh: Boolean): AppCheckToken = suspendCancellableCoroutine { cont ->
        ios.tokenForcingRefresh(forceRefresh) { token, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else {
                val expireTime = (token?.expirationDate() as? NSDate)?.timeIntervalSince1970?.toLong() ?: 0
                cont.resume(AppCheckToken(token?.token() ?: "", expireTime * 1000))
            }
        }
    }
    
    actual fun addAppCheckListener(listener: AppCheckListener) { /* iOS usa notificaciones */ }
    actual fun removeAppCheckListener(listener: AppCheckListener) {}
    actual fun setTokenAutoRefreshEnabled(enabled: Boolean) { ios.setIsTokenAutoRefreshEnabled(enabled) }
}

actual interface AppCheckProviderFactory
actual class AppCheckToken(actual val token: String, actual val expireTimeMillis: Long)
actual interface AppCheckListener { actual fun onAppCheckTokenChanged(token: AppCheckToken) }

@OptIn(ExperimentalForeignApi::class)
actual class PlayIntegrityAppCheckProviderFactory : AppCheckProviderFactory {
    actual companion object {
        actual fun getInstance(): PlayIntegrityAppCheckProviderFactory = PlayIntegrityAppCheckProviderFactory()
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class DebugAppCheckProviderFactory : AppCheckProviderFactory {
    actual companion object {
        actual fun getInstance(): DebugAppCheckProviderFactory = DebugAppCheckProviderFactory()
    }
}

