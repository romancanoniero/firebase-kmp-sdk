@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise
import kotlin.reflect.KClass

@JsModule("firebase/functions")
@JsNonModule
private external object FunctionsModule {
    fun getFunctions(): dynamic
    fun getFunctions(app: dynamic): dynamic
    fun getFunctions(app: dynamic, regionOrCustomDomain: String): dynamic
    fun httpsCallable(functions: dynamic, name: String): dynamic
    fun httpsCallable(functions: dynamic, name: String, options: dynamic): dynamic
    fun connectFunctionsEmulator(functions: dynamic, host: String, port: Int)
}

actual class FirebaseFunctions internal constructor(private val jsFn: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(FunctionsModule.getFunctions())
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions = FirebaseFunctions(FunctionsModule.getFunctions(app.js))
        actual fun getInstance(region: String): FirebaseFunctions = FirebaseFunctions(FunctionsModule.getFunctions(null, region))
        actual fun getInstance(app: FirebaseApp, region: String): FirebaseFunctions = FirebaseFunctions(FunctionsModule.getFunctions(app.js, region))
    }
    
    actual fun getHttpsCallable(name: String): HttpsCallableReference = HttpsCallableReference(FunctionsModule.httpsCallable(jsFn, name))
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference {
        val opts = js("{}"); opts.timeout = options.timeout
        return HttpsCallableReference(FunctionsModule.httpsCallable(jsFn, name, opts))
    }
    actual fun useEmulator(host: String, port: Int) { FunctionsModule.connectFunctionsEmulator(jsFn, host, port) }
}

actual class HttpsCallableReference internal constructor(private val jsCallable: dynamic) {
    actual suspend fun call(data: Any?): HttpsCallableResult {
        val result = (jsCallable(data) as Promise<dynamic>).await()
        return HttpsCallableResult(result)
    }
}

actual class HttpsCallableOptions internal constructor(
    actual val timeout: Long = 60000,
    actual val timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS
) {
    actual class Builder {
        private var timeout: Long = 60000
        private var unit: TimeUnit = TimeUnit.MILLISECONDS
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder { this.timeout = timeout; this.unit = unit; return this }
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions(timeout, unit)
    }
}

actual class HttpsCallableResult internal constructor(private val jsResult: dynamic) {
    actual val data: Any? get() = jsResult.data
    actual fun <T : Any> getData(clazz: KClass<T>): T? = jsResult.data as? T
}

actual enum class TimeUnit { NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS }

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

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}
