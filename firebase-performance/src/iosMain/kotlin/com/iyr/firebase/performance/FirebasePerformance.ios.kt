package com.iyr.firebase.performance

import cocoapods.FirebasePerformance.FIRPerformance
import cocoapods.FirebasePerformance.FIRTrace
import cocoapods.FirebasePerformance.FIRHTTPMetric
import cocoapods.FirebasePerformance.FIRHTTPMethod
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL

/**
 * FIRHTTPMethod enum values (from FIRHTTPMetric.h):
 * GET = 0, PUT = 1, POST = 2, DELETE = 3, HEAD = 4, PATCH = 5, OPTIONS = 6, TRACE = 7, CONNECT = 8
 */
@Suppress("UNCHECKED_CAST")
private fun httpMethodToFIR(method: String): FIRHTTPMethod = when (method.uppercase()) {
    "GET" -> 0
    "PUT" -> 1
    "POST" -> 2
    "DELETE" -> 3
    "HEAD" -> 4
    "PATCH" -> 5
    "OPTIONS" -> 6
    "TRACE" -> 7
    "CONNECT" -> 8
    else -> 0
} as FIRHTTPMethod

@OptIn(ExperimentalForeignApi::class)
actual class FirebasePerformance internal constructor(
    private val ios: FIRPerformance
) {
    actual companion object {
        actual fun getInstance(): FirebasePerformance = 
            FirebasePerformance(FIRPerformance.sharedInstance())
    }
    
    actual fun newTrace(traceName: String): Trace = 
        Trace(ios.traceWithName(traceName)!!)
    
    @Suppress("DEPRECATION_ERROR", "DEPRECATION")
    actual fun newHttpMetric(url: String, httpMethod: String): HttpMetric {
        val nsUrl = NSURL.URLWithString(url)!!
        val method = httpMethodToFIR(httpMethod)
        val metric = FIRHTTPMetric.alloc()?.initWithURL(URL = nsUrl, HTTPMethod = method)
        return HttpMetric(metric!!)
    }
    
    actual fun isPerformanceCollectionEnabled(): Boolean = ios.isDataCollectionEnabled()
    actual fun setPerformanceCollectionEnabled(enabled: Boolean) { 
        ios.setDataCollectionEnabled(enabled) 
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class Trace internal constructor(private val ios: FIRTrace) {
    actual fun start() { ios.start() }
    actual fun stop() { ios.stop() }
    actual fun putAttribute(attribute: String, value: String) { ios.setValue(value, forAttribute = attribute) }
    actual fun getAttribute(attribute: String): String? = ios.valueForAttribute(attribute)
    actual fun removeAttribute(attribute: String) { ios.removeAttribute(attribute) }
    @Suppress("UNCHECKED_CAST")
    actual fun getAttributes(): Map<String, String> = (ios.attributes() as? Map<String, String>) ?: emptyMap()
    actual fun incrementMetric(metricName: String, incrementBy: Long) { ios.incrementMetric(metricName, byInt = incrementBy) }
    actual fun getLongMetric(metricName: String): Long = ios.valueForIntMetric(metricName)
    actual fun putMetric(metricName: String, value: Long) { ios.setIntValue(value, forMetric = metricName) }
}

@OptIn(ExperimentalForeignApi::class)
actual class HttpMetric internal constructor(private val ios: FIRHTTPMetric) {
    actual fun start() { ios.start() }
    actual fun stop() { ios.stop() }
    actual fun setHttpResponseCode(responseCode: Int) { ios.setResponseCode(responseCode.toLong()) }
    actual fun setRequestPayloadSize(bytes: Long) { ios.setRequestPayloadSize(bytes) }
    actual fun setResponsePayloadSize(bytes: Long) { ios.setResponsePayloadSize(bytes) }
    actual fun setResponseContentType(contentType: String?) { ios.setResponseContentType(contentType) }
    actual fun putAttribute(attribute: String, value: String) { ios.setValue(value, forAttribute = attribute) }
    actual fun getAttribute(attribute: String): String? = ios.valueForAttribute(attribute)
    actual fun removeAttribute(attribute: String) { ios.removeAttribute(attribute) }
    @Suppress("UNCHECKED_CAST")
    actual fun getAttributes(): Map<String, String> = (ios.attributes() as? Map<String, String>) ?: emptyMap()
}
