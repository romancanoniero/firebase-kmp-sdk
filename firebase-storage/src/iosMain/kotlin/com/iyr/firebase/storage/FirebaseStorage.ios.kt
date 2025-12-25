package com.iyr.firebase.storage

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseStorage.*
import kotlinx.cinterop.*
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Foundation.timeIntervalSince1970
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ============================================================
// Conversiones ByteArray <-> NSData
// ============================================================

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(
        bytes = allocArrayOf(this@toNSData),
        length = this@toNSData.size.toULong()
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    
    return ByteArray(length).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}

// ============================================================
// FirebaseStorage
// ============================================================

actual class FirebaseStorage internal constructor(val ios: FIRStorage) {
    actual companion object {
        actual fun getInstance(): FirebaseStorage = FirebaseStorage(FIRStorage.storage())
        
        actual fun getInstance(app: FirebaseApp): FirebaseStorage {
            val appName = app.getName()
            return if (appName == "[DEFAULT]") {
                FirebaseStorage(FIRStorage.storage())
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance() después de configurar la app"
                )
            }
        }
        
        actual fun getInstance(url: String): FirebaseStorage = FirebaseStorage(FIRStorage.storageWithURL(url))
        
        actual fun getInstance(app: FirebaseApp, url: String): FirebaseStorage {
            val appName = app.getName()
            return if (appName == "[DEFAULT]") {
                FirebaseStorage(FIRStorage.storageWithURL(url))
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance(url) después de configurar la app"
                )
            }
        }
    }
    actual fun getReference(): StorageReference = StorageReference(ios.reference())
    actual fun getReference(location: String): StorageReference = StorageReference(ios.referenceWithPath(location))
    actual fun getReferenceFromUrl(fullUrl: String): StorageReference = StorageReference(ios.referenceForURL(fullUrl))
    actual fun setMaxUploadRetryTimeMillis(maxTransferRetryMillis: Long) { ios.setMaxUploadRetryTime(maxTransferRetryMillis.toDouble() / 1000.0) }
    actual fun setMaxDownloadRetryTimeMillis(maxTransferRetryMillis: Long) { ios.setMaxDownloadRetryTime(maxTransferRetryMillis.toDouble() / 1000.0) }
    actual fun setMaxOperationRetryTimeMillis(maxTransferRetryMillis: Long) { ios.setMaxOperationRetryTime(maxTransferRetryMillis.toDouble() / 1000.0) }
    actual fun useEmulator(host: String, port: Int) { ios.useEmulatorWithHost(host, port.toLong()) }
}

// ============================================================
// StorageReference
// ============================================================

actual class StorageReference internal constructor(val ios: FIRStorageReference) {
    actual val name: String get() = ios.name()
    actual val path: String get() = ios.fullPath()
    actual val bucket: String get() = ios.bucket()
    actual val parent: StorageReference? get() = ios.parent()?.let { StorageReference(it) }
    actual val root: StorageReference get() = StorageReference(ios.root())
    actual val storage: FirebaseStorage get() = FirebaseStorage(ios.storage())
    actual fun child(path: String): StorageReference = StorageReference(ios.child(path))
    
    actual suspend fun putBytes(bytes: ByteArray, metadata: StorageMetadata?): UploadTask {
        val nsData = bytes.toNSData()
        return UploadTask(ios.putData(nsData, metadata = metadata?.ios))
    }
    
    actual suspend fun putFile(uri: String, metadata: StorageMetadata?): UploadTask {
        val url = NSURL.fileURLWithPath(uri)
        return UploadTask(ios.putFile(url, metadata = metadata?.ios))
    }
    
    actual suspend fun getBytes(maxDownloadSizeBytes: Long): ByteArray = suspendCancellableCoroutine { cont ->
        ios.dataWithMaxSize(maxDownloadSizeBytes) { data, error ->
            if (error != null) {
                cont.resumeWithException(Exception(error.localizedDescription))
            } else {
                val result = data?.toByteArray() ?: ByteArray(0)
                cont.resume(result)
            }
        }
    }
    
    actual suspend fun getDownloadUrl(): String = suspendCancellableCoroutine { cont ->
        ios.downloadURLWithCompletion { url, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(url?.absoluteString ?: "")
        }
    }
    
    actual suspend fun delete(): Unit = suspendCancellableCoroutine { cont ->
        ios.deleteWithCompletion { error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun getMetadata(): StorageMetadata = suspendCancellableCoroutine { cont ->
        ios.metadataWithCompletion { meta, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(StorageMetadata(meta!!))
        }
    }
    
    actual suspend fun updateMetadata(metadata: StorageMetadata): StorageMetadata = suspendCancellableCoroutine { cont ->
        ios.updateMetadata(metadata.ios) { meta, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(StorageMetadata(meta!!))
        }
    }
    
    actual suspend fun listAll(): ListResult = suspendCancellableCoroutine { cont ->
        ios.listAllWithCompletion { result, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(ListResult(result!!))
        }
    }
    
    actual suspend fun list(maxResults: Int): ListResult = suspendCancellableCoroutine { cont ->
        ios.listWithMaxResults(maxResults.toLong()) { result, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(ListResult(result!!))
        }
    }
}

// ============================================================
// StorageMetadata
// ============================================================

actual class StorageMetadata internal constructor(val ios: FIRStorageMetadata) {
    actual val bucket: String? get() = ios.bucket()
    actual val cacheControl: String? get() = ios.cacheControl()
    actual val contentDisposition: String? get() = ios.contentDisposition()
    actual val contentEncoding: String? get() = ios.contentEncoding()
    actual val contentLanguage: String? get() = ios.contentLanguage()
    actual val contentType: String? get() = ios.contentType()
    actual val creationTimeMillis: Long get() = (ios.timeCreated()?.timeIntervalSince1970 ?: 0.0).toLong() * 1000
    actual val generation: String? get() = ios.generation()?.toString()
    actual val md5Hash: String? get() = ios.md5Hash()
    actual val metadataGeneration: String? get() = ios.metageneration()?.toString()
    actual val name: String? get() = ios.name()
    actual val path: String? get() = ios.path()
    actual val sizeBytes: Long get() = ios.size()
    actual val updatedTimeMillis: Long get() = (ios.updated()?.timeIntervalSince1970 ?: 0.0).toLong() * 1000
    
    @Suppress("UNCHECKED_CAST")
    actual val customMetadata: Map<String, String> get() = (ios.customMetadata() as? Map<String, String>) ?: emptyMap()
    
    actual class Builder {
        private val meta = FIRStorageMetadata()
        actual fun setCacheControl(cacheControl: String?): Builder { meta.setCacheControl(cacheControl); return this }
        actual fun setContentDisposition(contentDisposition: String?): Builder { meta.setContentDisposition(contentDisposition); return this }
        actual fun setContentEncoding(contentEncoding: String?): Builder { meta.setContentEncoding(contentEncoding); return this }
        actual fun setContentLanguage(contentLanguage: String?): Builder { meta.setContentLanguage(contentLanguage); return this }
        actual fun setContentType(contentType: String?): Builder { meta.setContentType(contentType); return this }
        
        @Suppress("UNCHECKED_CAST")
        actual fun setCustomMetadata(key: String, value: String?): Builder { 
            val current = (meta.customMetadata() as? MutableMap<Any?, Any?>) ?: mutableMapOf()
            if (value != null) current[key] = value else current.remove(key)
            meta.setCustomMetadata(current as Map<Any?, *>)
            return this 
        }
        actual fun build(): StorageMetadata = StorageMetadata(meta)
    }
}

// ============================================================
// UploadTask con await() implementado
// ============================================================

actual class UploadTask internal constructor(private val ios: FIRStorageUploadTask?) {
    private var _snapshot: TaskSnapshot = TaskSnapshot(0, 0, null, null)
    
    actual val snapshot: TaskSnapshot get() = _snapshot
    
    actual suspend fun await(): TaskSnapshot = suspendCancellableCoroutine { cont ->
        if (ios == null) {
            cont.resumeWithException(Exception("Upload task is null"))
            return@suspendCancellableCoroutine
        }
        
        var resumed = false
        
        // FIRStorageTaskStatus: 0=Unknown, 1=Resume, 2=Progress, 3=Pause, 4=Success, 5=Failure
        // Observar éxito (4)
        ios.observeStatus(4L) { snapshot: FIRStorageTaskSnapshot? ->
            if (!resumed) {
                resumed = true
                val meta = snapshot?.metadata()?.let { StorageMetadata(it) }
                val progress = snapshot?.progress()
                val result = TaskSnapshot(
                    bytesTransferred = progress?.completedUnitCount() ?: 0,
                    totalByteCount = progress?.totalUnitCount() ?: 0,
                    metadata = meta,
                    ref = snapshot?.reference()?.let { StorageReference(it) }
                )
                _snapshot = result
                cont.resume(result)
            }
        }
        
        // Observar error (5)
        ios.observeStatus(5L) { snapshot: FIRStorageTaskSnapshot? ->
            if (!resumed) {
                resumed = true
                val error = snapshot?.error()
                cont.resumeWithException(Exception(error?.localizedDescription ?: "Upload failed"))
            }
        }
        
        // Cancelación de coroutine
        cont.invokeOnCancellation {
            ios.cancel()
        }
    }
    
    actual fun addOnProgressListener(listener: (TaskSnapshot) -> Unit): UploadTask {
        // FIRStorageTaskStatusProgress = 2
        ios?.observeStatus(2L) { snapshot: FIRStorageTaskSnapshot? ->
            val progress = snapshot?.progress()
            val taskSnapshot = TaskSnapshot(
                bytesTransferred = progress?.completedUnitCount() ?: 0,
                totalByteCount = progress?.totalUnitCount() ?: 0,
                metadata = null,
                ref = snapshot?.reference()?.let { StorageReference(it) }
            )
            listener(taskSnapshot)
        }
        return this
    }
    
    actual fun addOnSuccessListener(listener: (TaskSnapshot) -> Unit): UploadTask {
        // FIRStorageTaskStatusSuccess = 4
        ios?.observeStatus(4L) { snapshot: FIRStorageTaskSnapshot? ->
            val meta = snapshot?.metadata()?.let { StorageMetadata(it) }
            val progress = snapshot?.progress()
            val taskSnapshot = TaskSnapshot(
                bytesTransferred = progress?.completedUnitCount() ?: 0,
                totalByteCount = progress?.totalUnitCount() ?: 0,
                metadata = meta,
                ref = snapshot?.reference()?.let { StorageReference(it) }
            )
            listener(taskSnapshot)
        }
        return this
    }
    
    actual fun addOnFailureListener(listener: (Exception) -> Unit): UploadTask {
        // FIRStorageTaskStatusFailure = 5
        ios?.observeStatus(5L) { snapshot: FIRStorageTaskSnapshot? ->
            val error = snapshot?.error()
            listener(Exception(error?.localizedDescription ?: "Upload failed"))
        }
        return this
    }
    
    actual fun cancel(): Boolean { ios?.cancel(); return true }
    actual fun pause(): Boolean { ios?.pause(); return true }
    actual fun resume(): Boolean { ios?.resume(); return true }
}

// ============================================================
// TaskSnapshot
// ============================================================

actual class TaskSnapshot(
    actual val bytesTransferred: Long,
    actual val totalByteCount: Long,
    actual val metadata: StorageMetadata?,
    private val ref: StorageReference?
) {
    actual val storage: StorageReference get() = ref ?: throw IllegalStateException("No reference")
    actual val uploadSessionUri: String? get() = null
}

// ============================================================
// ListResult
// ============================================================

actual class ListResult internal constructor(val ios: FIRStorageListResult) {
    @Suppress("UNCHECKED_CAST")
    actual val items: List<StorageReference> get() = (ios.items() as List<FIRStorageReference>).map { StorageReference(it) }
    
    @Suppress("UNCHECKED_CAST")
    actual val prefixes: List<StorageReference> get() = (ios.prefixes() as List<FIRStorageReference>).map { StorageReference(it) }
    
    actual val pageToken: String? get() = ios.pageToken()
}

// ============================================================
// StorageException
// ============================================================

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
