package com.iyr.firebase.performance

import com.google.firebase.perf.FirebasePerformance as AndroidPerformance
import com.google.firebase.perf.metrics.Trace as AndroidTrace
import com.google.firebase.perf.metrics.HttpMetric as AndroidHttpMetric

actual class FirebasePerformance internal constructor(
    private val android: AndroidPerformance
) {
    actual companion object {
        actual fun getInstance(): FirebasePerformance = 
            FirebasePerformance(AndroidPerformance.getInstance())
    }
    
    actual fun newTrace(traceName: String): Trace = Trace(android.newTrace(traceName))
    actual fun newHttpMetric(url: String, httpMethod: String): HttpMetric = 
        HttpMetric(android.newHttpMetric(url, httpMethod))
    actual fun isPerformanceCollectionEnabled(): Boolean = android.isPerformanceCollectionEnabled
    actual fun setPerformanceCollectionEnabled(enabled: Boolean) { 
        android.isPerformanceCollectionEnabled = enabled 
    }
}

actual class Trace internal constructor(private val android: AndroidTrace) {
    actual fun start() { android.start() }
    actual fun stop() { android.stop() }
    actual fun putAttribute(attribute: String, value: String) { android.putAttribute(attribute, value) }
    actual fun getAttribute(attribute: String): String? = android.getAttribute(attribute)
    actual fun removeAttribute(attribute: String) { android.removeAttribute(attribute) }
    actual fun getAttributes(): Map<String, String> = android.attributes
    actual fun incrementMetric(metricName: String, incrementBy: Long) { android.incrementMetric(metricName, incrementBy) }
    actual fun getLongMetric(metricName: String): Long = android.getLongMetric(metricName)
    actual fun putMetric(metricName: String, value: Long) { android.putMetric(metricName, value) }
}

actual class HttpMetric internal constructor(private val android: AndroidHttpMetric) {
    actual fun start() { android.start() }
    actual fun stop() { android.stop() }
    actual fun setHttpResponseCode(responseCode: Int) { android.setHttpResponseCode(responseCode) }
    actual fun setRequestPayloadSize(bytes: Long) { android.setRequestPayloadSize(bytes) }
    actual fun setResponsePayloadSize(bytes: Long) { android.setResponsePayloadSize(bytes) }
    actual fun setResponseContentType(contentType: String?) { android.setResponseContentType(contentType) }
    actual fun putAttribute(attribute: String, value: String) { android.putAttribute(attribute, value) }
    actual fun getAttribute(attribute: String): String? = android.getAttribute(attribute)
    actual fun removeAttribute(attribute: String) { android.removeAttribute(attribute) }
    actual fun getAttributes(): Map<String, String> = android.attributes
}






