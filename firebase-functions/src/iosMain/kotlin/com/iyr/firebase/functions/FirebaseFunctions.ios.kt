package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseFunctions.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseFunctions internal constructor(val ios: FIRFunctions) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(FIRFunctions.functions())
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions = FirebaseFunctions(FIRFunctions.functionsForApp(app.ios))
        actual fun getInstance(regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(FIRFunctions.functionsForRegion(regionOrCustomDomain))
        actual fun getInstance(app: FirebaseApp, regionOrCustomDomain: String): FirebaseFunctions = FirebaseFunctions(FIRFunctions.functionsForApp(app.ios, region = regionOrCustomDomain))
    }
    actual fun getHttpsCallable(name: String): HttpsCallableReference = HttpsCallableReference(ios.HTTPSCallableWithName(name))
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference(ios.HTTPSCallableWithName(name))
    actual fun getHttpsCallableFromUrl(url: String): HttpsCallableReference = HttpsCallableReference(ios.HTTPSCallableWithURL(NSURL.URLWithString(url)!!))
    actual fun getHttpsCallableFromUrl(url: String, options: HttpsCallableOptions): HttpsCallableReference = HttpsCallableReference(ios.HTTPSCallableWithURL(NSURL.URLWithString(url)!!))
    actual fun useEmulator(host: String, port: Int) { ios.useEmulatorWithHost(host, port.toLong()) }
}

actual class HttpsCallableReference internal constructor(private val ios: FIRHTTPSCallable) {
    actual suspend fun call(): HttpsCallableResult = suspendCancellableCoroutine { cont ->
        ios.callWithCompletion { result, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(HttpsCallableResult(result))
        }
    }
    actual suspend fun call(data: Any?): HttpsCallableResult = suspendCancellableCoroutine { cont ->
        ios.callWithObject(data) { result, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(HttpsCallableResult(result))
        }
    }
    actual fun withTimeout(timeout: Long, unit: TimeUnit): HttpsCallableReference {
        ios.setTimeoutInterval(unit.toSeconds(timeout))
        return this
    }
}

actual class HttpsCallableOptions {
    actual class Builder {
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder = this
        actual fun setLimitedUseAppCheckTokens(limitedUse: Boolean): Builder = this
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions()
    }
}

actual class HttpsCallableResult internal constructor(private val ios: FIRHTTPSCallableResult?) {
    actual val data: Any? get() = ios?.data()
}

actual enum class TimeUnit {
    NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS;
    fun toSeconds(value: Long): Double = when (this) {
        NANOSECONDS -> value / 1_000_000_000.0
        MICROSECONDS -> value / 1_000_000.0
        MILLISECONDS -> value / 1_000.0
        SECONDS -> value.toDouble()
        MINUTES -> value * 60.0
        HOURS -> value * 3600.0
        DAYS -> value * 86400.0
    }
}

actual class FirebaseFunctionsException(override val message: String, actual val code: Code, actual val details: Any?) : Exception(message) {
    actual enum class Code {
        OK, CANCELLED, UNKNOWN, INVALID_ARGUMENT, DEADLINE_EXCEEDED, NOT_FOUND,
        ALREADY_EXISTS, PERMISSION_DENIED, RESOURCE_EXHAUSTED, FAILED_PRECONDITION,
        ABORTED, OUT_OF_RANGE, UNIMPLEMENTED, INTERNAL, UNAVAILABLE, DATA_LOSS, UNAUTHENTICATED
    }
}
