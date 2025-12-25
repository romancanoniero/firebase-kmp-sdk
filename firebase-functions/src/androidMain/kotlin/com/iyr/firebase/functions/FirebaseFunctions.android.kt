package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.functions.FirebaseFunctions as AndroidFunctions
import com.google.firebase.functions.HttpsCallableReference as AndroidCallable
import com.google.firebase.functions.HttpsCallableResult as AndroidResult
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit as JavaTimeUnit
import kotlin.reflect.KClass

actual class FirebaseFunctions internal constructor(val android: AndroidFunctions) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(app.android))
        actual fun getInstance(region: String): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(region))
        actual fun getInstance(app: FirebaseApp, region: String): FirebaseFunctions = FirebaseFunctions(AndroidFunctions.getInstance(app.android, region))
    }
    
    actual fun getHttpsCallable(name: String): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallable(name))
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference(android.getHttpsCallable(name))
    actual fun useEmulator(host: String, port: Int) { android.useEmulator(host, port) }
}

actual class HttpsCallableReference internal constructor(private val android: AndroidCallable) {
    actual suspend fun call(data: Any?): HttpsCallableResult {
        val result = if (data != null) {
            android.call(data).await()
        } else {
            android.call().await()
        }
        return HttpsCallableResult(result)
    }
}

actual class HttpsCallableOptions private constructor(
    actual val timeout: Long,
    actual val timeoutUnit: TimeUnit
) {
    actual class Builder {
        private var timeout: Long = 60L
        private var unit: TimeUnit = TimeUnit.SECONDS
        
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder { 
            this.timeout = timeout
            this.unit = unit
            return this 
        }
        
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions(timeout, unit)
    }
}

actual class HttpsCallableResult internal constructor(private val android: AndroidResult) {
    actual val data: Any? get() = android.getData()
    
    @Suppress("UNCHECKED_CAST")
    actual fun <T : Any> getData(clazz: KClass<T>): T? = data as? T
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
}

actual class FirebaseFunctionsException actual constructor(
    message: String,
    actual val code: Code,
    actual val details: Any?
) : Exception(message) {
    
    actual enum class Code {
        OK, CANCELLED, UNKNOWN, INVALID_ARGUMENT, DEADLINE_EXCEEDED, NOT_FOUND,
        ALREADY_EXISTS, PERMISSION_DENIED, RESOURCE_EXHAUSTED, FAILED_PRECONDITION,
        ABORTED, OUT_OF_RANGE, UNIMPLEMENTED, INTERNAL, UNAVAILABLE, DATA_LOSS, UNAUTHENTICATED
    }
}
