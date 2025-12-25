package com.iyr.firebase.firestore

import cocoapods.FirebaseFirestoreInternal.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class CollectionReference internal constructor(
    private val iosRef: FIRCollectionReference
) : Query(iosRef) {
    
    actual val id: String get() = iosRef.collectionID
    actual val path: String get() = iosRef.path
    actual val parent: DocumentReference? get() = iosRef.parent?.let { DocumentReference(it) }
    
    actual fun document(): DocumentReference = DocumentReference(iosRef.documentWithAutoID())
    
    actual fun document(documentPath: String): DocumentReference = 
        DocumentReference(iosRef.documentWithPath(documentPath))
    
    @Suppress("UNCHECKED_CAST")
    actual suspend fun add(data: Map<String, Any?>): DocumentReference = suspendCancellableCoroutine { cont ->
        val docRef = iosRef.addDocumentWithData(data as Map<Any?, *>) { error ->
            if (error != null) cont.resumeWithException(error.toException())
        }
        if (docRef != null) cont.resume(DocumentReference(docRef))
    }
}

actual class DocumentReference internal constructor(val ios: FIRDocumentReference) {
    actual val id: String get() = ios.documentID
    actual val path: String get() = ios.path
    actual val parent: CollectionReference get() = CollectionReference(ios.parent)
    
    actual fun collection(collectionPath: String): CollectionReference = 
        CollectionReference(ios.collectionWithPath(collectionPath))
    
    actual suspend fun get(): DocumentSnapshot = suspendCancellableCoroutine { cont ->
        ios.getDocumentWithCompletion { snapshot, error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(DocumentSnapshot(snapshot!!))
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    actual suspend fun set(data: Map<String, Any?>, options: SetOptions): Unit = suspendCancellableCoroutine { cont ->
        val dataMap = data as Map<Any?, *>
        if (options.merge) {
            ios.setData(dataMap, merge = true) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        } else {
            ios.setData(dataMap) { error ->
                if (error != null) cont.resumeWithException(error.toException())
                else cont.resume(Unit)
            }
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    actual suspend fun update(data: Map<String, Any?>): Unit = suspendCancellableCoroutine { cont ->
        ios.updateData(data as Map<Any?, *>) { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun delete(): Unit = suspendCancellableCoroutine { cont ->
        ios.deleteDocumentWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual fun addSnapshotListener(listener: EventListener<DocumentSnapshot>): ListenerRegistration {
        val handle = ios.addSnapshotListener { snapshot, error ->
            listener.onEvent(
                snapshot?.let { DocumentSnapshot(it) }, 
                error?.let { FirestoreException(it.localizedDescription, 0) }
            )
        }
        return ListenerRegistration { handle?.remove() }
    }
    
    actual val snapshots: Flow<DocumentSnapshot> = callbackFlow {
        val handle = ios.addSnapshotListener { snapshot, error ->
            if (error != null) close(error.toException())
            else snapshot?.let { trySend(DocumentSnapshot(it)) }
        }
        awaitClose { handle?.remove() }
    }
}

actual class DocumentSnapshot internal constructor(val ios: FIRDocumentSnapshot) {
    actual val id: String get() = ios.documentID
    actual val reference: DocumentReference get() = DocumentReference(ios.reference)
    actual fun exists(): Boolean = ios.exists
    
    @Suppress("UNCHECKED_CAST")
    actual fun getData(): Map<String, Any?>? = ios.data() as? Map<String, Any?>
    
    actual fun get(field: String): Any? = ios.valueForField(field)
    
    actual fun contains(field: String): Boolean = ios.data()?.get(field) != null
    
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(ios.metadata)
}

actual class QuerySnapshot internal constructor(val ios: FIRQuerySnapshot) {
    @Suppress("UNCHECKED_CAST")
    actual val documents: List<DocumentSnapshot> get() = 
        (ios.documents as List<FIRDocumentSnapshot>).map { DocumentSnapshot(it) }
    
    @Suppress("UNCHECKED_CAST")
    actual val documentChanges: List<DocumentChange> get() = 
        (ios.documentChanges as List<FIRDocumentChange>).map { DocumentChange(it) }
    
    actual val isEmpty: Boolean get() = ios.isEmpty()
    actual val size: Int get() = ios.count().toInt()
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(ios.metadata)
}

actual class DocumentChange internal constructor(val ios: FIRDocumentChange) {
    actual val type: DocumentChangeType get() {
        val typeValue = ios.type
        return when {
            typeValue == FIRDocumentChangeType.FIRDocumentChangeTypeAdded -> DocumentChangeType.ADDED
            typeValue == FIRDocumentChangeType.FIRDocumentChangeTypeModified -> DocumentChangeType.MODIFIED
            typeValue == FIRDocumentChangeType.FIRDocumentChangeTypeRemoved -> DocumentChangeType.REMOVED
            else -> DocumentChangeType.MODIFIED
        }
    }
    actual val document: DocumentSnapshot get() = DocumentSnapshot(ios.document)
    actual val oldIndex: Int get() = ios.oldIndex.toInt()
    actual val newIndex: Int get() = ios.newIndex.toInt()
}

actual enum class DocumentChangeType { ADDED, MODIFIED, REMOVED }
actual enum class Direction { ASCENDING, DESCENDING }

actual class SnapshotMetadata internal constructor(val ios: FIRSnapshotMetadata) {
    actual val hasPendingWrites: Boolean get() = ios.hasPendingWrites()
    actual val isFromCache: Boolean get() = ios.isFromCache()
}

actual class SetOptions(val merge: Boolean = false, val fields: List<String>? = null) {
    actual companion object {
        actual fun overwrite(): SetOptions = SetOptions(false)
        actual fun merge(): SetOptions = SetOptions(true)
        actual fun mergeFields(vararg fields: String): SetOptions = SetOptions(true, fields.toList())
    }
}

actual class Transaction internal constructor(private val ios: FIRTransaction) {
    actual fun get(documentRef: DocumentReference): DocumentSnapshot = 
        DocumentSnapshot(ios.getDocument(documentRef.ios, null)!!)
    
    @Suppress("UNCHECKED_CAST")
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): Transaction {
        ios.setData(data as Map<Any?, *>, forDocument = documentRef.ios, merge = options.merge)
        return this
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): Transaction {
        ios.updateData(data as Map<Any?, *>, forDocument = documentRef.ios)
        return this
    }
    
    actual fun delete(documentRef: DocumentReference): Transaction {
        ios.deleteDocument(documentRef.ios)
        return this
    }
}

actual class WriteBatch internal constructor(private val ios: FIRWriteBatch) {
    @Suppress("UNCHECKED_CAST")
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): WriteBatch {
        ios.setData(data as Map<Any?, *>, forDocument = documentRef.ios, merge = options.merge)
        return this
    }
    
    @Suppress("UNCHECKED_CAST")
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): WriteBatch {
        ios.updateData(data as Map<Any?, *>, forDocument = documentRef.ios)
        return this
    }
    
    actual fun delete(documentRef: DocumentReference): WriteBatch {
        ios.deleteDocument(documentRef.ios)
        return this
    }
    
    actual suspend fun commit(): Unit = suspendCancellableCoroutine { cont ->
        ios.commitWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
}

actual fun interface EventListener<T> {
    actual fun onEvent(value: T?, error: FirestoreException?)
}

actual fun interface ListenerRegistration {
    actual fun remove()
}

actual class FirestoreException(
    message: String?, 
    actual val code: Int
) : Exception(message)

private fun NSError.toException() = Exception(localizedDescription)
