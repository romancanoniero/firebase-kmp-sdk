package com.iyr.firebase.firestore

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class FirebaseFirestore internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseFirestore = FirebaseFirestore(js("{}"))
        actual fun getInstance(app: FirebaseApp): FirebaseFirestore = FirebaseFirestore(js("{}"))
    }
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(js)
    actual fun document(documentPath: String): DocumentReference = DocumentReference(js)
    actual fun collectionGroup(collectionId: String): Query = Query(js)
    actual suspend fun <T> runTransaction(block: suspend Transaction.() -> T): T = TODO()
    actual fun batch(): WriteBatch = WriteBatch(js)
    actual suspend fun clearPersistence() {}
    actual suspend fun enableNetwork() {}
    actual suspend fun disableNetwork() {}
    actual suspend fun terminate() {}
    actual suspend fun waitForPendingWrites() {}
    actual fun useEmulator(host: String, port: Int) {}
}

actual open class Query internal constructor(open val js: dynamic) {
    actual fun whereEqualTo(field: String, value: Any?): Query = Query(js)
    actual fun whereNotEqualTo(field: String, value: Any?): Query = Query(js)
    actual fun whereLessThan(field: String, value: Any): Query = Query(js)
    actual fun whereLessThanOrEqualTo(field: String, value: Any): Query = Query(js)
    actual fun whereGreaterThan(field: String, value: Any): Query = Query(js)
    actual fun whereGreaterThanOrEqualTo(field: String, value: Any): Query = Query(js)
    actual fun whereArrayContains(field: String, value: Any): Query = Query(js)
    actual fun whereArrayContainsAny(field: String, values: List<Any>): Query = Query(js)
    actual fun whereIn(field: String, values: List<Any>): Query = Query(js)
    actual fun whereNotIn(field: String, values: List<Any>): Query = Query(js)
    actual fun orderBy(field: String, direction: Direction): Query = Query(js)
    actual fun limit(limit: Long): Query = Query(js)
    actual fun limitToLast(limit: Long): Query = Query(js)
    actual fun startAt(vararg fieldValues: Any): Query = Query(js)
    actual fun startAfter(vararg fieldValues: Any): Query = Query(js)
    actual fun endAt(vararg fieldValues: Any): Query = Query(js)
    actual fun endBefore(vararg fieldValues: Any): Query = Query(js)
    actual suspend fun get(): QuerySnapshot = QuerySnapshot(js)
    actual fun addSnapshotListener(listener: EventListener<QuerySnapshot>): ListenerRegistration = ListenerRegistration {}
    actual val snapshots: Flow<QuerySnapshot> = MutableStateFlow(QuerySnapshot(js))
}

actual class CollectionReference internal constructor(override val js: dynamic) : Query(js) {
    actual val id: String get() = js.id as? String ?: ""
    actual val path: String get() = js.path as? String ?: ""
    actual val parent: DocumentReference? get() = null
    actual fun document(): DocumentReference = DocumentReference(js)
    actual fun document(documentPath: String): DocumentReference = DocumentReference(js)
    actual suspend fun add(data: Map<String, Any?>): DocumentReference = DocumentReference(js)
}

actual class DocumentReference internal constructor(val js: dynamic) {
    actual val id: String get() = js.id as? String ?: ""
    actual val path: String get() = js.path as? String ?: ""
    actual val parent: CollectionReference get() = CollectionReference(js)
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(js)
    actual suspend fun get(): DocumentSnapshot = DocumentSnapshot(js)
    actual suspend fun set(data: Map<String, Any?>, options: SetOptions) {}
    actual suspend fun update(data: Map<String, Any?>) {}
    actual suspend fun delete() {}
    actual fun addSnapshotListener(listener: EventListener<DocumentSnapshot>): ListenerRegistration = ListenerRegistration {}
    actual val snapshots: Flow<DocumentSnapshot> = MutableStateFlow(DocumentSnapshot(js))
}

actual class DocumentSnapshot internal constructor(val js: dynamic) {
    actual val id: String get() = js.id as? String ?: ""
    actual val reference: DocumentReference get() = DocumentReference(js)
    actual fun exists(): Boolean = js.exists?.invoke() as? Boolean ?: false
    actual fun getData(): Map<String, Any?>? = null
    actual fun get(field: String): Any? = null
    actual fun contains(field: String): Boolean = false
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata()
}

actual class QuerySnapshot internal constructor(val js: dynamic) {
    actual val documents: List<DocumentSnapshot> get() = emptyList()
    actual val documentChanges: List<DocumentChange> get() = emptyList()
    actual val isEmpty: Boolean get() = true
    actual val size: Int get() = 0
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata()
}

actual class DocumentChange internal constructor(val js: dynamic) {
    actual val type: DocumentChangeType get() = DocumentChangeType.ADDED
    actual val document: DocumentSnapshot get() = DocumentSnapshot(js)
    actual val oldIndex: Int get() = 0
    actual val newIndex: Int get() = 0
}

actual enum class DocumentChangeType { ADDED, MODIFIED, REMOVED }
actual enum class Direction { ASCENDING, DESCENDING }
actual class SnapshotMetadata { actual val hasPendingWrites: Boolean get() = false; actual val isFromCache: Boolean get() = false }
actual class SetOptions { actual companion object { actual fun overwrite(): SetOptions = SetOptions(); actual fun merge(): SetOptions = SetOptions(); actual fun mergeFields(vararg fields: String): SetOptions = SetOptions() } }
actual class Transaction { actual fun get(documentRef: DocumentReference): DocumentSnapshot = DocumentSnapshot(js("{}")); actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): Transaction = this; actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): Transaction = this; actual fun delete(documentRef: DocumentReference): Transaction = this }
actual class WriteBatch internal constructor(val js: dynamic) { actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): WriteBatch = this; actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): WriteBatch = this; actual fun delete(documentRef: DocumentReference): WriteBatch = this; actual suspend fun commit() {} }
actual fun interface EventListener<T> { actual fun onEvent(value: T?, error: FirestoreException?) }
actual fun interface ListenerRegistration { actual fun remove() }
actual class FirestoreException(override val message: String, actual val code: Int) : Exception(message)
