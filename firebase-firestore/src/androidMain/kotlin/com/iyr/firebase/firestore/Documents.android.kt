package com.iyr.firebase.firestore

import com.google.firebase.firestore.CollectionReference as AndroidCollection
import com.google.firebase.firestore.DocumentReference as AndroidDocument
import com.google.firebase.firestore.DocumentSnapshot as AndroidDocSnapshot
import com.google.firebase.firestore.QuerySnapshot as AndroidQuerySnapshot
import com.google.firebase.firestore.DocumentChange as AndroidDocChange
import com.google.firebase.firestore.Transaction as AndroidTransaction
import com.google.firebase.firestore.WriteBatch as AndroidBatch
import com.google.firebase.firestore.SetOptions as AndroidSetOptions
import com.google.firebase.firestore.SnapshotMetadata as AndroidMetadata
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class CollectionReference internal constructor(
    override val android: AndroidCollection
) : Query(android) {
    actual val id: String get() = android.id
    actual val path: String get() = android.path
    actual val parent: DocumentReference? get() = android.parent?.let { DocumentReference(it) }
    actual fun document(): DocumentReference = DocumentReference(android.document())
    actual fun document(documentPath: String): DocumentReference = DocumentReference(android.document(documentPath))
    actual suspend fun add(data: Map<String, Any?>): DocumentReference = DocumentReference(android.add(data).await())
}

actual class DocumentReference internal constructor(val android: AndroidDocument) {
    actual val id: String get() = android.id
    actual val path: String get() = android.path
    actual val parent: CollectionReference get() = CollectionReference(android.parent)
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(android.collection(collectionPath))
    actual suspend fun get(): DocumentSnapshot = DocumentSnapshot(android.get().await())
    actual suspend fun set(data: Map<String, Any?>, options: SetOptions) { android.set(data, options.android).await() }
    actual suspend fun update(data: Map<String, Any?>) { android.update(data).await() }
    actual suspend fun delete() { android.delete().await() }
    actual fun addSnapshotListener(listener: EventListener<DocumentSnapshot>): ListenerRegistration {
        val reg = android.addSnapshotListener { s, e -> listener.onEvent(s?.let { DocumentSnapshot(it) }, e?.let { FirestoreException(it) }) }
        return ListenerRegistration { reg.remove() }
    }
    actual val snapshots: Flow<DocumentSnapshot> = callbackFlow {
        val reg = android.addSnapshotListener { s, e -> if (e != null) close(e) else s?.let { trySend(DocumentSnapshot(it)) } }
        awaitClose { reg.remove() }
    }
}

actual class DocumentSnapshot internal constructor(val android: AndroidDocSnapshot) {
    actual val id: String get() = android.id
    actual val reference: DocumentReference get() = DocumentReference(android.reference)
    actual fun exists(): Boolean = android.exists()
    actual fun getData(): Map<String, Any?>? = android.data
    actual fun get(field: String): Any? = android.get(field)
    actual fun contains(field: String): Boolean = android.contains(field)
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(android.metadata)
}

actual class QuerySnapshot internal constructor(val android: AndroidQuerySnapshot) {
    actual val documents: List<DocumentSnapshot> get() = android.documents.map { DocumentSnapshot(it) }
    actual val documentChanges: List<DocumentChange> get() = android.documentChanges.map { DocumentChange(it) }
    actual val isEmpty: Boolean get() = android.isEmpty
    actual val size: Int get() = android.size()
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(android.metadata)
}

actual class DocumentChange internal constructor(val android: AndroidDocChange) {
    actual val type: DocumentChangeType get() = when (android.type) {
        AndroidDocChange.Type.ADDED -> DocumentChangeType.ADDED
        AndroidDocChange.Type.MODIFIED -> DocumentChangeType.MODIFIED
        AndroidDocChange.Type.REMOVED -> DocumentChangeType.REMOVED
    }
    actual val document: DocumentSnapshot get() = DocumentSnapshot(android.document)
    actual val oldIndex: Int get() = android.oldIndex
    actual val newIndex: Int get() = android.newIndex
}

actual enum class DocumentChangeType { ADDED, MODIFIED, REMOVED }
actual enum class Direction { ASCENDING, DESCENDING }

actual class SnapshotMetadata internal constructor(val android: AndroidMetadata) {
    actual val hasPendingWrites: Boolean get() = android.hasPendingWrites()
    actual val isFromCache: Boolean get() = android.isFromCache
}

actual class SetOptions internal constructor(val android: AndroidSetOptions) {
    actual companion object {
        actual fun overwrite(): SetOptions = SetOptions(AndroidSetOptions.merge()) // No direct overwrite in Android
        actual fun merge(): SetOptions = SetOptions(AndroidSetOptions.merge())
        actual fun mergeFields(vararg fields: String): SetOptions = SetOptions(AndroidSetOptions.mergeFields(*fields))
    }
}

actual class Transaction internal constructor(private val android: AndroidTransaction) {
    actual fun get(documentRef: DocumentReference): DocumentSnapshot = DocumentSnapshot(android.get(documentRef.android))
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): Transaction { android.set(documentRef.android, data, options.android); return this }
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): Transaction { android.update(documentRef.android, data); return this }
    actual fun delete(documentRef: DocumentReference): Transaction { android.delete(documentRef.android); return this }
}

actual class WriteBatch internal constructor(private val android: AndroidBatch) {
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): WriteBatch { android.set(documentRef.android, data, options.android); return this }
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): WriteBatch { android.update(documentRef.android, data); return this }
    actual fun delete(documentRef: DocumentReference): WriteBatch { android.delete(documentRef.android); return this }
    actual suspend fun commit() { android.commit().await() }
}

actual fun interface EventListener<T> { actual fun onEvent(value: T?, error: FirestoreException?) }
actual fun interface ListenerRegistration { actual fun remove() }
actual class FirestoreException internal constructor(val android: FirebaseFirestoreException) : Exception(android.message) {
    actual val code: Int get() = android.code.ordinal
}
