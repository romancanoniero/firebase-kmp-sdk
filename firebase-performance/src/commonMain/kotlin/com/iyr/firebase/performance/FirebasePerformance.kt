package com.iyr.firebase.performance

expect class FirebasePerformance {
    companion object {
        fun getInstance(): FirebasePerformance
    }
    
    fun newTrace(traceName: String): Trace
    fun newHttpMetric(url: String, httpMethod: String): HttpMetric
    fun isPerformanceCollectionEnabled(): Boolean
    fun setPerformanceCollectionEnabled(enabled: Boolean)
}

expect class Trace {
    fun start()
    fun stop()
    fun putAttribute(attribute: String, value: String)
    fun getAttribute(attribute: String): String?
    fun removeAttribute(attribute: String)
    fun getAttributes(): Map<String, String>
    fun incrementMetric(metricName: String, incrementBy: Long)
    fun getLongMetric(metricName: String): Long
    fun putMetric(metricName: String, value: Long)
}

expect class HttpMetric {
    fun start()
    fun stop()
    fun setHttpResponseCode(responseCode: Int)
    fun setRequestPayloadSize(bytes: Long)
    fun setResponsePayloadSize(bytes: Long)
    fun setResponseContentType(contentType: String?)
    fun putAttribute(attribute: String, value: String)
    fun getAttribute(attribute: String): String?
    fun removeAttribute(attribute: String)
    fun getAttributes(): Map<String, String>
}






