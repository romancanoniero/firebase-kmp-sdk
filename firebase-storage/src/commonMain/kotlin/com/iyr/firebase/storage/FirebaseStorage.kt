package com.iyr.firebase.storage

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow

expect class FirebaseStorage {
    companion object {
        fun getInstance(): FirebaseStorage
        fun getInstance(app: FirebaseApp): FirebaseStorage
        fun getInstance(url: String): FirebaseStorage
        fun getInstance(app: FirebaseApp, url: String): FirebaseStorage
    }
    fun getReference(): StorageReference
    fun getReference(location: String): StorageReference
    fun getReferenceFromUrl(fullUrl: String): StorageReference
    fun setMaxUploadRetryTimeMillis(maxTransferRetryMillis: Long)
    fun setMaxDownloadRetryTimeMillis(maxTransferRetryMillis: Long)
    fun setMaxOperationRetryTimeMillis(maxTransferRetryMillis: Long)
    fun useEmulator(host: String, port: Int)
}

expect class StorageReference {
    val name: String
    val path: String
    val bucket: String
    val parent: StorageReference?
    val root: StorageReference
    val storage: FirebaseStorage
    fun child(path: String): StorageReference
    suspend fun putBytes(bytes: ByteArray, metadata: StorageMetadata? = null): UploadTask
    suspend fun putFile(uri: String, metadata: StorageMetadata? = null): UploadTask
    suspend fun getBytes(maxDownloadSizeBytes: Long): ByteArray
    suspend fun getDownloadUrl(): String
    suspend fun delete()
    suspend fun getMetadata(): StorageMetadata
    suspend fun updateMetadata(metadata: StorageMetadata): StorageMetadata
    suspend fun listAll(): ListResult
    suspend fun list(maxResults: Int): ListResult
}

expect class StorageMetadata {
    val bucket: String?
    val cacheControl: String?
    val contentDisposition: String?
    val contentEncoding: String?
    val contentLanguage: String?
    val contentType: String?
    val creationTimeMillis: Long
    val generation: String?
    val md5Hash: String?
    val metadataGeneration: String?
    val name: String?
    val path: String?
    val sizeBytes: Long
    val updatedTimeMillis: Long
    val customMetadata: Map<String, String>
    
    class Builder {
        fun setCacheControl(cacheControl: String?): Builder
        fun setContentDisposition(contentDisposition: String?): Builder
        fun setContentEncoding(contentEncoding: String?): Builder
        fun setContentLanguage(contentLanguage: String?): Builder
        fun setContentType(contentType: String?): Builder
        fun setCustomMetadata(key: String, value: String?): Builder
        fun build(): StorageMetadata
    }
}

expect class UploadTask {
    val snapshot: TaskSnapshot
    suspend fun await(): TaskSnapshot
    fun addOnProgressListener(listener: (TaskSnapshot) -> Unit): UploadTask
    fun addOnSuccessListener(listener: (TaskSnapshot) -> Unit): UploadTask
    fun addOnFailureListener(listener: (Exception) -> Unit): UploadTask
    fun cancel(): Boolean
    fun pause(): Boolean
    fun resume(): Boolean
}

expect class TaskSnapshot {
    val bytesTransferred: Long
    val totalByteCount: Long
    val metadata: StorageMetadata?
    val storage: StorageReference
    val uploadSessionUri: String?
}

expect class ListResult {
    val items: List<StorageReference>
    val prefixes: List<StorageReference>
    val pageToken: String?
}

expect class StorageException : Exception {
    val errorCode: Int
    val httpResultCode: Int
    companion object {
        val ERROR_UNKNOWN: Int
        val ERROR_OBJECT_NOT_FOUND: Int
        val ERROR_BUCKET_NOT_FOUND: Int
        val ERROR_PROJECT_NOT_FOUND: Int
        val ERROR_QUOTA_EXCEEDED: Int
        val ERROR_NOT_AUTHENTICATED: Int
        val ERROR_NOT_AUTHORIZED: Int
        val ERROR_RETRY_LIMIT_EXCEEDED: Int
        val ERROR_INVALID_CHECKSUM: Int
        val ERROR_CANCELED: Int
    }
}
