package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import kotlin.reflect.KClass

expect class FirebaseFunctions {
    companion object {
        fun getInstance(): FirebaseFunctions
        fun getInstance(app: FirebaseApp): FirebaseFunctions
        fun getInstance(region: String): FirebaseFunctions
        fun getInstance(app: FirebaseApp, region: String): FirebaseFunctions
    }
    
    fun getHttpsCallable(name: String): HttpsCallableReference
    fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference
    fun useEmulator(host: String, port: Int)
}

expect class HttpsCallableReference {
    suspend fun call(data: Any? = null): HttpsCallableResult
}

expect class HttpsCallableOptions {
    val timeout: Long
    val timeoutUnit: TimeUnit
    
    class Builder {
        fun setTimeout(timeout: Long, unit: TimeUnit): Builder
        fun build(): HttpsCallableOptions
    }
}

expect class HttpsCallableResult {
    val data: Any?
    fun <T : Any> getData(clazz: KClass<T>): T?
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

expect class FirebaseFunctionsException(
    message: String,
    code: Code,
    details: Any? = null
) : Exception {
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
