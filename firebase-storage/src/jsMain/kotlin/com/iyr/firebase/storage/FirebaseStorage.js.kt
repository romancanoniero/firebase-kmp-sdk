package com.iyr.firebase.storage

import com.iyr.firebase.core.FirebaseApp

actual class FirebaseStorage internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseStorage = FirebaseStorage(js("{}"))
        actual fun getInstance(app: FirebaseApp): FirebaseStorage = FirebaseStorage(js("{}"))
        actual fun getInstance(url: String): FirebaseStorage = FirebaseStorage(js("{}"))
        actual fun getInstance(app: FirebaseApp, url: String): FirebaseStorage = FirebaseStorage(js("{}"))
    }
    actual fun getReference(): StorageReference = StorageReference(js)
    actual fun getReference(location: String): StorageReference = StorageReference(js)
    actual fun getReferenceFromUrl(fullUrl: String): StorageReference = StorageReference(js)
    actual fun setMaxUploadRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun setMaxDownloadRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun setMaxOperationRetryTimeMillis(maxTransferRetryMillis: Long) {}
    actual fun useEmulator(host: String, port: Int) {}
}

actual class StorageReference internal constructor(val js: dynamic) {
    actual val name: String get() = js.name as? String ?: ""
    actual val path: String get() = js.fullPath as? String ?: ""
    actual val bucket: String get() = js.bucket as? String ?: ""
    actual val parent: StorageReference? get() = null
    actual val root: StorageReference get() = this
    actual val storage: FirebaseStorage get() = FirebaseStorage(js)
    actual fun child(path: String): StorageReference = StorageReference(js)
    actual suspend fun putBytes(bytes: ByteArray, metadata: StorageMetadata?): UploadTask = UploadTask()
    actual suspend fun putFile(uri: String, metadata: StorageMetadata?): UploadTask = UploadTask()
    actual suspend fun getBytes(maxDownloadSizeBytes: Long): ByteArray = ByteArray(0)
    actual suspend fun getDownloadUrl(): String = ""
    actual suspend fun delete() {}
    actual suspend fun getMetadata(): StorageMetadata = StorageMetadata()
    actual suspend fun updateMetadata(metadata: StorageMetadata): StorageMetadata = metadata
    actual suspend fun listAll(): ListResult = ListResult()
    actual suspend fun list(maxResults: Int): ListResult = ListResult()
}

actual class StorageMetadata {
    actual val bucket: String? get() = null
    actual val cacheControl: String? get() = null
    actual val contentDisposition: String? get() = null
    actual val contentEncoding: String? get() = null
    actual val contentLanguage: String? get() = null
    actual val contentType: String? get() = null
    actual val creationTimeMillis: Long get() = 0
    actual val generation: String? get() = null
    actual val md5Hash: String? get() = null
    actual val metadataGeneration: String? get() = null
    actual val name: String? get() = null
    actual val path: String? get() = null
    actual val sizeBytes: Long get() = 0
    actual val updatedTimeMillis: Long get() = 0
    actual val customMetadata: Map<String, String> get() = emptyMap()
    actual class Builder {
        actual fun setCacheControl(cacheControl: String?): Builder = this
        actual fun setContentDisposition(contentDisposition: String?): Builder = this
        actual fun setContentEncoding(contentEncoding: String?): Builder = this
        actual fun setContentLanguage(contentLanguage: String?): Builder = this
        actual fun setContentType(contentType: String?): Builder = this
        actual fun setCustomMetadata(key: String, value: String?): Builder = this
        actual fun build(): StorageMetadata = StorageMetadata()
    }
}

actual class UploadTask {
    actual val snapshot: TaskSnapshot get() = TaskSnapshot()
    actual suspend fun await(): TaskSnapshot = TaskSnapshot()
    actual fun addOnProgressListener(listener: (TaskSnapshot) -> Unit): UploadTask = this
    actual fun addOnSuccessListener(listener: (TaskSnapshot) -> Unit): UploadTask = this
    actual fun addOnFailureListener(listener: (Exception) -> Unit): UploadTask = this
    actual fun cancel(): Boolean = true
    actual fun pause(): Boolean = true
    actual fun resume(): Boolean = true
}

actual class TaskSnapshot {
    actual val bytesTransferred: Long get() = 0
    actual val totalByteCount: Long get() = 0
    actual val metadata: StorageMetadata? get() = null
    actual val storage: StorageReference get() = StorageReference(js("{}"))
    actual val uploadSessionUri: String? get() = null
}

actual class ListResult {
    actual val items: List<StorageReference> get() = emptyList()
    actual val prefixes: List<StorageReference> get() = emptyList()
    actual val pageToken: String? get() = null
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
