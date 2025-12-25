@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.storage

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise
import kotlin.js.Date

@JsModule("firebase/storage")
@JsNonModule
private external object StorageModule {
    fun getStorage(): dynamic
    fun getStorage(app: dynamic): dynamic
    fun getStorage(app: dynamic, bucketUrl: String): dynamic
    fun ref(storage: dynamic, path: String?): dynamic
    fun uploadBytes(ref: dynamic, data: dynamic, metadata: dynamic?): Promise<dynamic>
    fun getDownloadURL(ref: dynamic): Promise<dynamic>
    fun getMetadata(ref: dynamic): Promise<dynamic>
    fun updateMetadata(ref: dynamic, metadata: dynamic): Promise<dynamic>
    fun deleteObject(ref: dynamic): Promise<dynamic>
    fun list(ref: dynamic, options: dynamic?): Promise<dynamic>
    fun listAll(ref: dynamic): Promise<dynamic>
    fun getBytes(ref: dynamic, maxDownloadSizeBytes: Long): Promise<dynamic>
    fun connectStorageEmulator(storage: dynamic, host: String, port: Int)
}

actual class FirebaseStorage internal constructor(internal val jsStorage: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseStorage = FirebaseStorage(StorageModule.getStorage())
        actual fun getInstance(app: FirebaseApp): FirebaseStorage = FirebaseStorage(StorageModule.getStorage(app.js))
        actual fun getInstance(url: String): FirebaseStorage = FirebaseStorage(StorageModule.getStorage(null, url))
        actual fun getInstance(app: FirebaseApp, url: String): FirebaseStorage = FirebaseStorage(StorageModule.getStorage(app.js, url))
    }
    
    actual fun getReference(): StorageReference = StorageReference(StorageModule.ref(jsStorage, null), this)
    actual fun getReference(location: String): StorageReference = StorageReference(StorageModule.ref(jsStorage, location), this)
    actual fun getReferenceFromUrl(fullUrl: String): StorageReference = StorageReference(StorageModule.ref(jsStorage, fullUrl), this)
    actual fun setMaxUploadRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun setMaxDownloadRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun setMaxOperationRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun useEmulator(host: String, port: Int) { StorageModule.connectStorageEmulator(jsStorage, host, port) }
}

actual class StorageReference internal constructor(
    internal val jsRef: dynamic,
    private val storageInstance: FirebaseStorage
) {
    actual val name: String get() = jsRef.name as String
    actual val path: String get() = jsRef.fullPath as String
    actual val bucket: String get() = jsRef.bucket as String
    actual val parent: StorageReference? get() = jsRef.parent?.let { p -> StorageReference(p, storageInstance) }
    actual val root: StorageReference get() = StorageReference(jsRef.root, storageInstance)
    actual val storage: FirebaseStorage get() = storageInstance
    
    actual fun child(path: String): StorageReference = StorageReference(StorageModule.ref(jsRef, path), storageInstance)
    
    actual suspend fun putBytes(bytes: ByteArray, metadata: StorageMetadata?): UploadTask {
        val jsData = js("new Uint8Array(bytes)")
        StorageModule.uploadBytes(jsRef, jsData, metadata?.toJsObject()).await()
        return UploadTask()
    }
    
    actual suspend fun putFile(uri: String, metadata: StorageMetadata?): UploadTask = UploadTask()
    
    actual suspend fun getBytes(maxDownloadSizeBytes: Long): ByteArray {
        val result = StorageModule.getBytes(jsRef, maxDownloadSizeBytes).await()
        return result as ByteArray
    }
    
    actual suspend fun getDownloadUrl(): String = StorageModule.getDownloadURL(jsRef).await() as String
    actual suspend fun delete() { StorageModule.deleteObject(jsRef).await() }
    actual suspend fun getMetadata(): StorageMetadata = StorageMetadata(StorageModule.getMetadata(jsRef).await())
    actual suspend fun updateMetadata(metadata: StorageMetadata): StorageMetadata = StorageMetadata(StorageModule.updateMetadata(jsRef, metadata.toJsObject()).await())
    
    actual suspend fun listAll(): ListResult {
        val result = StorageModule.listAll(jsRef).await()
        return ListResult(result, storageInstance)
    }
    
    actual suspend fun list(maxResults: Int): ListResult {
        val opts = js("{}"); opts.maxResults = maxResults
        val result = StorageModule.list(jsRef, opts).await()
        return ListResult(result, storageInstance)
    }
}

actual class StorageMetadata internal constructor(private val jsMeta: dynamic? = null) {
    actual val bucket: String? get() = jsMeta?.bucket as? String
    actual val cacheControl: String? get() = jsMeta?.cacheControl as? String
    actual val contentDisposition: String? get() = jsMeta?.contentDisposition as? String
    actual val contentEncoding: String? get() = jsMeta?.contentEncoding as? String
    actual val contentLanguage: String? get() = jsMeta?.contentLanguage as? String
    actual val contentType: String? get() = jsMeta?.contentType as? String
    actual val creationTimeMillis: Long get() = (jsMeta?.timeCreated as? String)?.let { Date.parse(it).toLong() } ?: 0
    actual val generation: String? get() = jsMeta?.generation as? String
    actual val md5Hash: String? get() = jsMeta?.md5Hash as? String
    actual val metadataGeneration: String? get() = jsMeta?.metageneration as? String
    actual val name: String? get() = jsMeta?.name as? String
    actual val path: String? get() = jsMeta?.fullPath as? String
    actual val sizeBytes: Long get() = (jsMeta?.size as? Number)?.toLong() ?: 0
    actual val updatedTimeMillis: Long get() = (jsMeta?.updated as? String)?.let { Date.parse(it).toLong() } ?: 0
    actual val customMetadata: Map<String, String> get() = jsMeta?.customMetadata as? Map<String, String> ?: emptyMap()
    
    internal fun toJsObject(): dynamic {
        val obj = js("{}")
        cacheControl?.let { obj.cacheControl = it }
        contentDisposition?.let { obj.contentDisposition = it }
        contentEncoding?.let { obj.contentEncoding = it }
        contentLanguage?.let { obj.contentLanguage = it }
        contentType?.let { obj.contentType = it }
        return obj
    }
    
    actual class Builder {
        private var contentType: String? = null
        actual fun setCacheControl(cacheControl: String?): Builder = this
        actual fun setContentDisposition(contentDisposition: String?): Builder = this
        actual fun setContentEncoding(contentEncoding: String?): Builder = this
        actual fun setContentLanguage(contentLanguage: String?): Builder = this
        actual fun setContentType(contentType: String?): Builder { this.contentType = contentType; return this }
        actual fun setCustomMetadata(key: String, value: String?): Builder = this
        actual fun build(): StorageMetadata = StorageMetadata()
    }
}

actual class UploadTask internal constructor() {
    actual val snapshot: TaskSnapshot get() = TaskSnapshot(0, 0, null, null)
    actual suspend fun await(): TaskSnapshot = TaskSnapshot(0, 0, null, null)
    actual fun addOnProgressListener(listener: (TaskSnapshot) -> Unit): UploadTask = this
    actual fun addOnSuccessListener(listener: (TaskSnapshot) -> Unit): UploadTask = this
    actual fun addOnFailureListener(listener: (Exception) -> Unit): UploadTask = this
    actual fun cancel(): Boolean = true
    actual fun pause(): Boolean = true
    actual fun resume(): Boolean = true
}

actual class TaskSnapshot internal constructor(
    actual val bytesTransferred: Long,
    actual val totalByteCount: Long,
    actual val metadata: StorageMetadata?,
    private val ref: StorageReference?
) {
    actual val storage: StorageReference get() = ref ?: throw IllegalStateException("No reference")
    actual val uploadSessionUri: String? get() = null
}

actual class ListResult internal constructor(private val jsResult: dynamic, private val storage: FirebaseStorage) {
    actual val items: List<StorageReference> get() = (jsResult.items as Array<dynamic>).map { r -> StorageReference(r, storage) }
    actual val prefixes: List<StorageReference> get() = (jsResult.prefixes as Array<dynamic>).map { r -> StorageReference(r, storage) }
    actual val pageToken: String? get() = jsResult.nextPageToken as? String
}

actual class StorageException : Exception() {
    actual val errorCode: Int get() = 0
    actual val httpResultCode: Int get() = 0
    actual companion object {
        actual val ERROR_UNKNOWN: Int = -1
        actual val ERROR_OBJECT_NOT_FOUND: Int = -2
        actual val ERROR_BUCKET_NOT_FOUND: Int = -3
        actual val ERROR_PROJECT_NOT_FOUND: Int = -4
        actual val ERROR_QUOTA_EXCEEDED: Int = -5
        actual val ERROR_NOT_AUTHENTICATED: Int = -6
        actual val ERROR_NOT_AUTHORIZED: Int = -7
        actual val ERROR_RETRY_LIMIT_EXCEEDED: Int = -8
        actual val ERROR_INVALID_CHECKSUM: Int = -9
        actual val ERROR_CANCELED: Int = -10
    }
}

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}

private fun <T> Promise<T>.then(onFulfilled: (T) -> Unit, onRejected: (Throwable) -> Unit): Promise<T> {
    asDynamic().then(onFulfilled, onRejected)
    return this
}
