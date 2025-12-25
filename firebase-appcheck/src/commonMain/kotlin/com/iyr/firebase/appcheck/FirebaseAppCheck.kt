package com.iyr.firebase.appcheck

import com.iyr.firebase.core.FirebaseApp

expect class FirebaseAppCheck {
    companion object {
        fun getInstance(): FirebaseAppCheck
        fun getInstance(app: FirebaseApp): FirebaseAppCheck
    }
    
    fun installAppCheckProviderFactory(factory: AppCheckProviderFactory)
    suspend fun getToken(forceRefresh: Boolean): AppCheckToken
    fun addAppCheckListener(listener: AppCheckListener)
    fun removeAppCheckListener(listener: AppCheckListener)
    fun setTokenAutoRefreshEnabled(enabled: Boolean)
}

expect interface AppCheckProviderFactory

expect class AppCheckToken {
    val token: String
    val expireTimeMillis: Long
}

expect interface AppCheckListener {
    fun onAppCheckTokenChanged(token: AppCheckToken)
}

expect class PlayIntegrityAppCheckProviderFactory : AppCheckProviderFactory {
    companion object {
        fun getInstance(): PlayIntegrityAppCheckProviderFactory
    }
}

expect class DebugAppCheckProviderFactory : AppCheckProviderFactory {
    companion object {
        fun getInstance(): DebugAppCheckProviderFactory
    }
}

