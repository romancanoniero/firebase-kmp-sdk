package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp

actual class FirebaseFunctions internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(js("{}"))
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions = FirebaseFunctions(js("{}"))
        actual fun getInstance(regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(js("{}"))
        actual fun getInstance(app: FirebaseApp, regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(js("{}"))
    }
    actual fun getHttpsCallable(name: String): HttpsCallableReference = HttpsCallableReference()
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference()
    actual fun getHttpsCallableFromUrl(url: String): HttpsCallableReference = HttpsCallableReference()
    actual fun getHttpsCallableFromUrl(url: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference()
    actual fun useEmulator(host: String, port: Int) {}
}

actual class HttpsCallableReference {
    actual suspend fun call(): HttpsCallableResult = HttpsCallableResult()
    actual suspend fun call(data: Any?): HttpsCallableResult = HttpsCallableResult()
    actual fun withTimeout(timeout: Long, unit: TimeUnit): HttpsCallableReference = this
}

actual class HttpsCallableOptions {
    actual class Builder {
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder = this
        actual fun setLimitedUseAppCheckTokens(limitedUse: Boolean): Builder = this
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions()
    }
}

actual class HttpsCallableResult {
    actual val data: Any? get() = null
    actual fun <T> getData(clazz: Class<T>): T? = null
}

actual enum class TimeUnit { NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS }

actual class FirebaseFunctionsException(override val message: String, actual val code: Code, actual val details: Any?) : Exception(message) {
    actual enum class Code {
        OK, CANCELLED, UNKNOWN, INVALID_ARGUMENT, DEADLINE_EXCEEDED, NOT_FOUND,
        ALREADY_EXISTS, PERMISSION_DENIED, RESOURCE_EXHAUSTED, FAILED_PRECONDITION,
        ABORTED, OUT_OF_RANGE, UNIMPLEMENTED, INTERNAL, UNAVAILABLE, DATA_LOSS, UNAUTHENTICATED
    }
}

// JS doesn't have Class<T>, provide a simple typealias
typealias Class<T> = kotlin.reflect.KClass<T>
