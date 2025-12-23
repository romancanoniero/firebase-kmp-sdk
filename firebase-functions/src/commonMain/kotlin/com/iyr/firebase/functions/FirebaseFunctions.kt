package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp

expect class FirebaseFunctions {
    companion object {
        fun getInstance(): FirebaseFunctions
        fun getInstance(app: FirebaseApp): FirebaseFunctions
        fun getInstance(regionOrCustomDomain: String): FirebaseFunctions
        fun getInstance(app: FirebaseApp, regionOrCustomDomain: String): FirebaseFunctions
    }
    
    fun getHttpsCallable(name: String): HttpsCallableReference
    fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference
    fun getHttpsCallableFromUrl(url: String): HttpsCallableReference
    fun getHttpsCallableFromUrl(url: String, options: HttpsCallableOptions): HttpsCallableReference
    fun useEmulator(host: String, port: Int)
}

expect class HttpsCallableReference {
    suspend fun call(): HttpsCallableResult
    suspend fun call(data: Any?): HttpsCallableResult
    fun withTimeout(timeout: Long, unit: TimeUnit): HttpsCallableReference
}

expect class HttpsCallableOptions {
    class Builder {
        fun setTimeout(timeout: Long, unit: TimeUnit): Builder
        fun setLimitedUseAppCheckTokens(limitedUse: Boolean): Builder
        fun build(): HttpsCallableOptions
    }
}

expect class HttpsCallableResult {
    val data: Any?
    fun <T> getData(clazz: Class<T>): T?
}

expect enum class TimeUnit {
    NANOSECONDS,
    MICROSECONDS,
    MILLISECONDS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS
}

expect class FirebaseFunctionsException : Exception {
    val code: Code
    val details: Any?
    
    enum class Code {
        OK,
        CANCELLED,
        UNKNOWN,
        INVALID_ARGUMENT,
        DEADLINE_EXCEEDED,
        NOT_FOUND,
        ALREADY_EXISTS,
        PERMISSION_DENIED,
        RESOURCE_EXHAUSTED,
        FAILED_PRECONDITION,
        ABORTED,
        OUT_OF_RANGE,
        UNIMPLEMENTED,
        INTERNAL,
        UNAVAILABLE,
        DATA_LOSS,
        UNAUTHENTICATED
    }
}
