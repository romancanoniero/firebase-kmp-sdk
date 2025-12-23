package com.iyr.firebase.storage

import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.storage.FirebaseStorage as AndroidStorage
import com.google.firebase.storage.StorageReference as AndroidRef
import com.google.firebase.storage.StorageMetadata as AndroidMetadata
import com.google.firebase.storage.UploadTask as AndroidUpload
import com.google.firebase.storage.ListResult as AndroidListResult
import com.google.firebase.storage.StorageException as AndroidStorageException
import android.net.Uri
import kotlinx.coroutines.tasks.await

actual class FirebaseStorage internal constructor(val android: AndroidStorage) {
    actual companion object {
        actual fun getInstance(): FirebaseStorage = FirebaseStorage(AndroidStorage.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseStorage = FirebaseStorage(AndroidStorage.getInstance(app.android))
        actual fun getInstance(url: String): FirebaseStorage = FirebaseStorage(AndroidStorage.getInstance(url))
        actual fun getInstance(app: FirebaseApp, url: String): FirebaseStorage = FirebaseStorage(AndroidStorage.getInstance(app.android, url))
    }
    actual fun getReference(): StorageReference = StorageReference(android.reference)
    actual fun getReference(location: String): StorageReference = StorageReference(android.getReference(location))
    actual fun getReferenceFromUrl(fullUrl: String): StorageReference = StorageReference(android.getReferenceFromUrl(fullUrl))
    actual fun setMaxUploadRetryTimeMillis(maxTransferRetryMillis: Long) { android.maxUploadRetryTimeMillis = maxTransferRetryMillis }
    actual fun setMaxDownloadRetryTimeMillis(maxTransferRetryMillis: Long) { android.maxDownloadRetryTimeMillis = maxTransferRetryMillis }
    actual fun setMaxOperationRetryTimeMillis(maxTransferRetryMillis: Long) { android.maxOperationRetryTimeMillis = maxTransferRetryMillis }
    actual fun useEmulator(host: String, port: Int) { android.useEmulator(host, port) }
}

actual class StorageReference internal constructor(val android: AndroidRef) {
    actual val name: String get() = android.name
    actual val path: String get() = android.path
    actual val bucket: String get() = android.bucket
    actual val parent: StorageReference? get() = android.parent?.let { StorageReference(it) }
    actual val root: StorageReference get() = StorageReference(android.root)
    actual val storage: FirebaseStorage get() = FirebaseStorage(android.storage)
    actual fun child(path: String): StorageReference = StorageReference(android.child(path))
    actual suspend fun putBytes(bytes: ByteArray, metadata: StorageMetadata?): UploadTask {
        val task = if (metadata != null) android.putBytes(bytes, metadata.android) else android.putBytes(bytes)
        return UploadTask(task)
    }
    actual suspend fun putFile(uri: String, metadata: StorageMetadata?): UploadTask {
        val task = if (metadata != null) android.putFile(Uri.parse(uri), metadata.android) else android.putFile(Uri.parse(uri))
        return UploadTask(task)
    }
    actual suspend fun getBytes(maxDownloadSizeBytes: Long): ByteArray = android.getBytes(maxDownloadSizeBytes).await()
    actual suspend fun getDownloadUrl(): String = android.downloadUrl.await().toString()
    actual suspend fun delete() { android.delete().await() }
    actual suspend fun getMetadata(): StorageMetadata = StorageMetadata(android.metadata.await())
    actual suspend fun updateMetadata(metadata: StorageMetadata): StorageMetadata = StorageMetadata(android.updateMetadata(metadata.android).await())
    actual suspend fun listAll(): ListResult = ListResult(android.listAll().await())
    actual suspend fun list(maxResults: Int): ListResult = ListResult(android.list(maxResults).await())
}

actual class StorageMetadata internal constructor(val android: AndroidMetadata) {
    actual val bucket: String? get() = android.bucket
    actual val cacheControl: String? get() = android.cacheControl
    actual val contentDisposition: String? get() = android.contentDisposition
    actual val contentEncoding: String? get() = android.contentEncoding
    actual val contentLanguage: String? get() = android.contentLanguage
    actual val contentType: String? get() = android.contentType
    actual val creationTimeMillis: Long get() = android.creationTimeMillis
    actual val generation: String? get() = android.generation
    actual val md5Hash: String? get() = android.md5Hash
    actual val metadataGeneration: String? get() = android.metadataGeneration
    actual val name: String? get() = android.name
    actual val path: String? get() = android.path
    actual val sizeBytes: Long get() = android.sizeBytes
    actual val updatedTimeMillis: Long get() = android.updatedTimeMillis
    actual val customMetadata: Map<String, String> get() = android.customMetadataKeys.associateWith { android.getCustomMetadata(it) ?: "" }
    
    actual class Builder {
        private val builder = AndroidMetadata.Builder()
        actual fun setCacheControl(cacheControl: String?): Builder { builder.setCacheControl(cacheControl); return this }
        actual fun setContentDisposition(contentDisposition: String?): Builder { builder.setContentDisposition(contentDisposition); return this }
        actual fun setContentEncoding(contentEncoding: String?): Builder { builder.setContentEncoding(contentEncoding); return this }
        actual fun setContentLanguage(contentLanguage: String?): Builder { builder.setContentLanguage(contentLanguage); return this }
        actual fun setContentType(contentType: String?): Builder { builder.setContentType(contentType); return this }
        actual fun setCustomMetadata(key: String, value: String?): Builder { builder.setCustomMetadata(key, value); return this }
        actual fun build(): StorageMetadata = StorageMetadata(builder.build())
    }
}

actual class UploadTask internal constructor(private val android: AndroidUpload) {
    actual val snapshot: TaskSnapshot get() = TaskSnapshot(android.snapshot)
    actual suspend fun await(): TaskSnapshot = TaskSnapshot(android.await())
    actual fun addOnProgressListener(listener: (TaskSnapshot) -> Unit): UploadTask { android.addOnProgressListener { listener(TaskSnapshot(it)) }; return this }
    actual fun addOnSuccessListener(listener: (TaskSnapshot) -> Unit): UploadTask { android.addOnSuccessListener { listener(TaskSnapshot(it)) }; return this }
    actual fun addOnFailureListener(listener: (Exception) -> Unit): UploadTask { android.addOnFailureListener { listener(it) }; return this }
    actual fun cancel(): Boolean = android.cancel()
    actual fun pause(): Boolean = android.pause()
    actual fun resume(): Boolean = android.resume()
}

actual class TaskSnapshot internal constructor(val android: com.google.firebase.storage.UploadTask.TaskSnapshot) {
    actual val bytesTransferred: Long get() = android.bytesTransferred
    actual val totalByteCount: Long get() = android.totalByteCount
    actual val metadata: StorageMetadata? get() = android.metadata?.let { StorageMetadata(it) }
    actual val storage: StorageReference get() = StorageReference(android.storage)
    actual val uploadSessionUri: String? get() = android.uploadSessionUri?.toString()
}

actual class ListResult internal constructor(val android: AndroidListResult) {
    actual val items: List<StorageReference> get() = android.items.map { StorageReference(it) }
    actual val prefixes: List<StorageReference> get() = android.prefixes.map { StorageReference(it) }
    actual val pageToken: String? get() = android.pageToken
}

actual class StorageException : Exception() {
    actual val errorCode: Int get() = 0
    actual val httpResultCode: Int get() = 0
    actual companion object {
        actual val ERROR_UNKNOWN: Int = AndroidStorageException.ERROR_UNKNOWN
        actual val ERROR_OBJECT_NOT_FOUND: Int = AndroidStorageException.ERROR_OBJECT_NOT_FOUND
        actual val ERROR_BUCKET_NOT_FOUND: Int = AndroidStorageException.ERROR_BUCKET_NOT_FOUND
        actual val ERROR_PROJECT_NOT_FOUND: Int = AndroidStorageException.ERROR_PROJECT_NOT_FOUND
        actual val ERROR_QUOTA_EXCEEDED: Int = AndroidStorageException.ERROR_QUOTA_EXCEEDED
        actual val ERROR_NOT_AUTHENTICATED: Int = AndroidStorageException.ERROR_NOT_AUTHENTICATED
        actual val ERROR_NOT_AUTHORIZED: Int = AndroidStorageException.ERROR_NOT_AUTHORIZED
        actual val ERROR_RETRY_LIMIT_EXCEEDED: Int = AndroidStorageException.ERROR_RETRY_LIMIT_EXCEEDED
        actual val ERROR_INVALID_CHECKSUM: Int = AndroidStorageException.ERROR_INVALID_CHECKSUM
        actual val ERROR_CANCELED: Int = AndroidStorageException.ERROR_CANCELED
    }
}
