package com.iyr.firebase.core

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise

/**
 * Awaits for Promise completion and returns its result.
 * This bridges JavaScript Promises to Kotlin Coroutines.
 */
suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then(
        onFulfilled = { value ->
            cont.resume(value)
            value
        },
        onRejected = { error ->
            cont.resumeWithException(error.asThrowable())
            null
        }
    )
}

/**
 * Converts a dynamic JS error to a Kotlin Throwable
 */
fun dynamic.asThrowable(): Throwable {
    return when {
        this is Throwable -> this
        else -> Exception(this?.toString() ?: "Unknown error")
    }
}

/**
 * Type-safe wrapper for JS Promise
 */
@JsName("Promise")
external class JsPromise<T> {
    fun then(onFulfilled: (T) -> dynamic): JsPromise<dynamic>
    fun then(onFulfilled: (T) -> dynamic, onRejected: (dynamic) -> dynamic): JsPromise<dynamic>
    fun catch(onRejected: (dynamic) -> dynamic): JsPromise<T>
}
