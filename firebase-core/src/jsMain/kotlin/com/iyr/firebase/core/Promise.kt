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
            val throwable = when (error) {
                is Throwable -> error
                else -> Exception(error?.toString() ?: "Unknown error")
            }
            cont.resumeWithException(throwable)
            null
        }
    )
}

/**
 * Converts a dynamic JS error to a Kotlin Throwable
 */
fun jsErrorToThrowable(error: dynamic): Throwable {
    return when {
        error is Throwable -> error
        else -> Exception(error?.toString() ?: "Unknown error")
    }
}
