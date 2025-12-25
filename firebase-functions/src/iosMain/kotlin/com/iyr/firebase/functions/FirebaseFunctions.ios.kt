package com.iyr.firebase.functions

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseFunctions.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseFunctions internal constructor(val ios: FIRFunctions) {
    actual companion object {
        actual fun getInstance(): FirebaseFunctions = FirebaseFunctions(FIRFunctions.functions())
        
        actual fun getInstance(app: FirebaseApp): FirebaseFunctions {
            // Evitar conflictos de tipos entre módulos - usar default
            val appName = app.getName()
            return if (appName == "[DEFAULT]") {
                FirebaseFunctions(FIRFunctions.functions())
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance() después de configurar la app"
                )
            }
        }
        
        actual fun getInstance(region: String): FirebaseFunctions = 
            FirebaseFunctions(FIRFunctions.functionsForRegion(region))
            
        actual fun getInstance(app: FirebaseApp, region: String): FirebaseFunctions {
            val appName = app.getName()
            return if (appName == "[DEFAULT]") {
                FirebaseFunctions(FIRFunctions.functionsForRegion(region))
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance(region) después de configurar la app"
                )
            }
        }
    }
    
    actual fun getHttpsCallable(name: String): HttpsCallableReference = 
        HttpsCallableReference(ios.HTTPSCallableWithName(name))
        
    actual fun getHttpsCallable(name: String, options: HttpsCallableOptions): HttpsCallableReference = 
        HttpsCallableReference(ios.HTTPSCallableWithName(name))
        
    actual fun useEmulator(host: String, port: Int) { 
        ios.useEmulatorWithHost(host, port.toLong()) 
    }
}

actual class HttpsCallableReference internal constructor(private val ios: FIRHTTPSCallable) {
    actual suspend fun call(data: Any?): HttpsCallableResult = suspendCancellableCoroutine { cont ->
        if (data != null) {
            ios.callWithObject(data) { result, error ->
                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
                else cont.resume(HttpsCallableResult(result))
            }
        } else {
            ios.callWithCompletion { result, error ->
                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
                else cont.resume(HttpsCallableResult(result))
            }
        }
    }
}

actual class HttpsCallableOptions private constructor() {
    actual val timeout: Long = 60L
    actual val timeoutUnit: TimeUnit = TimeUnit.SECONDS
    
    actual class Builder {
        private var timeout: Long = 60L
        private var unit: TimeUnit = TimeUnit.SECONDS
        
        actual fun setTimeout(timeout: Long, unit: TimeUnit): Builder {
            this.timeout = timeout
            this.unit = unit
            return this
        }
        actual fun build(): HttpsCallableOptions = HttpsCallableOptions()
    }
}

actual class HttpsCallableResult internal constructor(private val ios: FIRHTTPSCallableResult?) {
    actual val data: Any? get() = ios?.data()
    
    @Suppress("UNCHECKED_CAST")
    actual fun <T : Any> getData(clazz: kotlin.reflect.KClass<T>): T? = data as? T
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
