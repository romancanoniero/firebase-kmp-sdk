package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions as AndroidFunctions
import com.google.firebase.functions.HttpsCallableReference as AndroidCallable
import com.google.firebase.functions.HttpsCallableResult as AndroidResult
import com.google.firebase.functions.FirebaseFunctionsException as AndroidException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit as JavaTimeUnit

actual class FirebaseFunctions internal constructor(val android: AndroidFunctions) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(app.android))
        actual fun getInstance(regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(regionOrCustomDomain))
        actual fun getInstance(app: FirebaseApp, regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(app.android, regionOrCustomDomain))
    }
    actual fun getHttpsCallable(name: String): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallable(name))
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallable(name))
    actual fun getHttpsCallableFromUrl(url: String): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallableFromUrl(java.net.URL(url)))
    actual fun getHttpsCallableFromUrl(url: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallableFromUrl(java.net.URL(url)))
    actual fun useEmulator(host: String, port: Int) { android.useEmulator(host, port) }
}

actual class HttpsCallableReference internal constructor(private val android: AndroidCallable) {
    actual suspend fun call(): HttpsCallableResult = HttpsCallableResult(android.call().await())
    actual suspend fun call(data: Any?): HttpsCallableResult = HttpsCallableResult(android.call(data).await())
    actual fun withTimeout(timeout: Long, unit: TimeUnit): HttpsCallableReference {
        android.withTimeout(timeout, unit.toJava())
        return this
    }
}

actual class HttpsCallableOptions internal constructor() {
    actual class Builder {
        private var timeout: Long = 60000
        private var limitedUse: Boolean = false
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder { this.timeout = unit.toMillis(timeout); return this }
        actual fun setLimitedUseAppCheckTokens(limitedUse: Boolean): Builder { this.limitedUse = limitedUse; return this }
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions()
    }
}

actual class HttpsCallableResult internal constructor(private val android: AndroidResult) {
    actual val data: Any? get() = android.getData()
}

actual enum class TimeUnit {
    NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS;
    
    fun toJava(): JavaTimeUnit = when (this) {
        NANOSECONDS -> JavaTimeUnit.NANOSECONDS
        MICROSECONDS -> JavaTimeUnit.MICROSECONDS
        MILLISECONDS -> JavaTimeUnit.MILLISECONDS
        SECONDS -> JavaTimeUnit.SECONDS
        MINUTES -> JavaTimeUnit.MINUTES
        HOURS -> JavaTimeUnit.HOURS
        DAYS -> JavaTimeUnit.DAYS
    }
    
    fun toMillis(duration: Long): Long = toJava().toMillis(duration)
}

actual class FirebaseFunctionsException internal constructor(private val android: AndroidException) : Exception(android.message) {
    actual val code: Code get() = Code.valueOf(android.code.name)
    actual val details: Any? get() = android.details
    
    actual enum class Code {
        OK, CANCELLED, UNKNOWN, INVALID_ARGUMENT, DEADLINE_EXCEEDED, NOT_FOUND,
        ALREADY_EXISTS, PERMISSION_DENIED, RESOURCE_EXHAUSTED, FAILED_PRECONDITION,
        ABORTED, OUT_OF_RANGE, UNIMPLEMENTED, INTERNAL, UNAVAILABLE, DATA_LOSS, UNAUTHENTICATED
    }
}
