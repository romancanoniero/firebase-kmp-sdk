@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.firestore

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.Promise

@JsModule("firebase/firestore")
@JsNonModule
private external object FirestoreModule {
    fun getFirestore(): dynamic
    fun getFirestore(app: dynamic): dynamic
    fun collection(db: dynamic, path: String): dynamic
    fun doc(db: dynamic, path: String): dynamic
    fun addDoc(ref: dynamic, data: dynamic): Promise<dynamic>
    fun setDoc(ref: dynamic, data: dynamic): Promise<dynamic>
    fun setDoc(ref: dynamic, data: dynamic, options: dynamic): Promise<dynamic>
    fun updateDoc(ref: dynamic, data: dynamic): Promise<dynamic>
    fun deleteDoc(ref: dynamic): Promise<dynamic>
    fun getDoc(ref: dynamic): Promise<dynamic>
    fun getDocs(query: dynamic): Promise<dynamic>
    fun onSnapshot(ref: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun query(ref: dynamic, vararg constraints: dynamic): dynamic
    fun where(field: String, op: String, value: Any?): dynamic
    fun orderBy(field: String, direction: String?): dynamic
    fun limit(n: Int): dynamic
    fun limitToLast(n: Int): dynamic
    fun startAt(vararg values: Any?): dynamic
    fun startAfter(vararg values: Any?): dynamic
    fun endAt(vararg values: Any?): dynamic
    fun endBefore(vararg values: Any?): dynamic
    fun writeBatch(db: dynamic): dynamic
    fun runTransaction(db: dynamic, fn: (dynamic) -> Promise<dynamic>): Promise<dynamic>
    fun collectionGroup(db: dynamic, id: String): dynamic
    fun enableNetwork(db: dynamic): Promise<dynamic>
    fun disableNetwork(db: dynamic): Promise<dynamic>
    fun clearIndexedDbPersistence(db: dynamic): Promise<dynamic>
    fun terminate(db: dynamic): Promise<dynamic>
    fun waitForPendingWrites(db: dynamic): Promise<dynamic>
    fun connectFirestoreEmulator(db: dynamic, host: String, port: Int)
}

actual class FirebaseFirestore internal constructor(internal val jsDb: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseFirestore = FirebaseFirestore(FirestoreModule.getFirestore())
        actual fun getInstance(app: FirebaseApp): FirebaseFirestore = FirebaseFirestore(FirestoreModule.getFirestore(app.js))
    }
    
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(FirestoreModule.collection(jsDb, collectionPath))
    actual fun document(documentPath: String): DocumentReference = DocumentReference(FirestoreModule.doc(jsDb, documentPath))
    actual fun collectionGroup(collectionId: String): Query = Query(FirestoreModule.collectionGroup(jsDb, collectionId))
    
    actual suspend fun <T> runTransaction(block: suspend Transaction.() -> T): T {
        // JS Firestore transactions require synchronous callbacks
        // For now, we use a simplified approach
        throw UnsupportedOperationException("Transactions with suspend blocks are not directly supported in JS. Use batch() instead.")
    }
    
    actual fun batch(): WriteBatch = WriteBatch(FirestoreModule.writeBatch(jsDb))
    actual suspend fun clearPersistence() { FirestoreModule.clearIndexedDbPersistence(jsDb).await() }
    actual suspend fun enableNetwork() { FirestoreModule.enableNetwork(jsDb).await() }
    actual suspend fun disableNetwork() { FirestoreModule.disableNetwork(jsDb).await() }
    actual suspend fun terminate() { FirestoreModule.terminate(jsDb).await() }
    actual suspend fun waitForPendingWrites() { FirestoreModule.waitForPendingWrites(jsDb).await() }
    actual fun useEmulator(host: String, port: Int) { FirestoreModule.connectFirestoreEmulator(jsDb, host, port) }
}

actual open class Query internal constructor(internal open val jsQuery: dynamic) {
    actual fun whereEqualTo(field: String, value: Any?): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "==", value)))
    actual fun whereNotEqualTo(field: String, value: Any?): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "!=", value)))
    actual fun whereLessThan(field: String, value: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "<", value)))
    actual fun whereLessThanOrEqualTo(field: String, value: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "<=", value)))
    actual fun whereGreaterThan(field: String, value: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, ">", value)))
    actual fun whereGreaterThanOrEqualTo(field: String, value: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, ">=", value)))
    actual fun whereArrayContains(field: String, value: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "array-contains", value)))
    actual fun whereArrayContainsAny(field: String, values: List<Any>): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "array-contains-any", values.toTypedArray())))
    actual fun whereIn(field: String, values: List<Any>): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "in", values.toTypedArray())))
    actual fun whereNotIn(field: String, values: List<Any>): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.where(field, "not-in", values.toTypedArray())))
    actual fun orderBy(field: String, direction: Direction): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.orderBy(field, if (direction == Direction.DESCENDING) "desc" else "asc")))
    actual fun limit(limit: Long): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.limit(limit.toInt())))
    actual fun limitToLast(limit: Long): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.limitToLast(limit.toInt())))
    actual fun startAt(vararg fieldValues: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.startAt(*fieldValues)))
    actual fun startAfter(vararg fieldValues: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.startAfter(*fieldValues)))
    actual fun endAt(vararg fieldValues: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.endAt(*fieldValues)))
    actual fun endBefore(vararg fieldValues: Any): Query = Query(FirestoreModule.query(jsQuery, FirestoreModule.endBefore(*fieldValues)))
    
    actual suspend fun get(): QuerySnapshot {
        val result = FirestoreModule.getDocs(jsQuery).await()
        return QuerySnapshot(result)
    }
    
    actual fun addSnapshotListener(listener: EventListener<QuerySnapshot>): ListenerRegistration {
        val unsub = FirestoreModule.onSnapshot(jsQuery) { snap -> listener.onEvent(QuerySnapshot(snap), null) }
        return object : ListenerRegistration { override fun remove() { unsub() } }
    }
    
    actual val snapshots: Flow<QuerySnapshot> = callbackFlow {
        val unsub = FirestoreModule.onSnapshot(jsQuery) { snap -> trySend(QuerySnapshot(snap)) }
        awaitClose { unsub() }
    }
}

actual class CollectionReference internal constructor(override val jsQuery: dynamic) : Query(jsQuery) {
    actual val id: String get() = jsQuery.id as String
    actual val path: String get() = jsQuery.path as String
    actual val parent: DocumentReference? get() = jsQuery.parent?.let { p -> DocumentReference(p) }
    actual fun document(): DocumentReference = DocumentReference(FirestoreModule.doc(jsQuery.firestore, jsQuery.path + "/" + generateId()))
    actual fun document(documentPath: String): DocumentReference = DocumentReference(FirestoreModule.doc(jsQuery.firestore, jsQuery.path + "/" + documentPath))
    actual suspend fun add(data: Map<String, Any?>): DocumentReference {
        val result = FirestoreModule.addDoc(jsQuery, data.toJsObject()).await()
        return DocumentReference(result)
    }
    private fun generateId(): String = js("Math.random().toString(36).substring(2, 15)") as String
}

actual class DocumentReference internal constructor(internal val jsDoc: dynamic) {
    actual val id: String get() = jsDoc.id as String
    actual val path: String get() = jsDoc.path as String
    actual val parent: CollectionReference get() = CollectionReference(jsDoc.parent)
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(FirestoreModule.collection(jsDoc.firestore, jsDoc.path + "/" + collectionPath))
    
    actual suspend fun get(): DocumentSnapshot = DocumentSnapshot(FirestoreModule.getDoc(jsDoc).await())
    
    actual suspend fun set(data: Map<String, Any?>, options: SetOptions) {
        if (options.merge) {
            val jsOpts = js("{}"); jsOpts.merge = true
            FirestoreModule.setDoc(jsDoc, data.toJsObject(), jsOpts).await()
        } else {
            FirestoreModule.setDoc(jsDoc, data.toJsObject()).await()
        }
    }
    
    actual suspend fun update(data: Map<String, Any?>) { FirestoreModule.updateDoc(jsDoc, data.toJsObject()).await() }
    actual suspend fun delete() { FirestoreModule.deleteDoc(jsDoc).await() }
    
    actual fun addSnapshotListener(listener: EventListener<DocumentSnapshot>): ListenerRegistration {
        val unsub = FirestoreModule.onSnapshot(jsDoc) { snap -> listener.onEvent(DocumentSnapshot(snap), null) }
        return object : ListenerRegistration { override fun remove() { unsub() } }
    }
    
    actual val snapshots: Flow<DocumentSnapshot> = callbackFlow {
        val unsub = FirestoreModule.onSnapshot(jsDoc) { snap -> trySend(DocumentSnapshot(snap)) }
        awaitClose { unsub() }
    }
}

actual class DocumentSnapshot internal constructor(internal val jsSnap: dynamic) {
    actual val id: String get() = jsSnap.id as String
    actual val reference: DocumentReference get() = DocumentReference(jsSnap.ref)
    actual fun exists(): Boolean = jsSnap.exists() as Boolean
    actual fun getData(): Map<String, Any?>? = jsSnap.data() as? Map<String, Any?>
    actual fun get(field: String): Any? = jsSnap.get(field)
    actual fun contains(field: String): Boolean = getData()?.containsKey(field) == true
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(jsSnap.metadata)
}

actual class QuerySnapshot internal constructor(internal val jsSnap: dynamic) {
    actual val documents: List<DocumentSnapshot> get() = (jsSnap.docs as Array<dynamic>).map { d -> DocumentSnapshot(d) }
    actual val documentChanges: List<DocumentChange> get() = (jsSnap.docChanges() as Array<dynamic>).map { c -> DocumentChange(c) }
    actual val isEmpty: Boolean get() = jsSnap.empty as Boolean
    actual val size: Int get() = jsSnap.size as Int
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(jsSnap.metadata)
}

actual class DocumentChange internal constructor(private val jsChange: dynamic) {
    actual val type: DocumentChangeType get() = when (jsChange.type as String) {
        "added" -> DocumentChangeType.ADDED
        "modified" -> DocumentChangeType.MODIFIED
        "removed" -> DocumentChangeType.REMOVED
        else -> DocumentChangeType.MODIFIED
    }
    actual val document: DocumentSnapshot get() = DocumentSnapshot(jsChange.doc)
    actual val oldIndex: Int get() = jsChange.oldIndex as Int
    actual val newIndex: Int get() = jsChange.newIndex as Int
}

actual enum class DocumentChangeType { ADDED, MODIFIED, REMOVED }
actual enum class Direction { ASCENDING, DESCENDING }

actual class SnapshotMetadata internal constructor(private val jsMeta: dynamic) {
    actual val hasPendingWrites: Boolean get() = jsMeta.hasPendingWrites as Boolean
    actual val isFromCache: Boolean get() = jsMeta.fromCache as Boolean
}

actual class SetOptions internal constructor(internal val merge: Boolean = false, internal val fields: List<String>? = null) {
    actual companion object {
        actual fun overwrite(): SetOptions = SetOptions(false)
        actual fun merge(): SetOptions = SetOptions(true)
        actual fun mergeFields(vararg fields: String): SetOptions = SetOptions(true, fields.toList())
    }
}

actual class Transaction internal constructor(private val jsTx: dynamic) {
    actual fun get(documentRef: DocumentReference): DocumentSnapshot = DocumentSnapshot(jsTx.get(documentRef.jsDoc))
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): Transaction {
        if (options.merge) { val o = js("{}"); o.merge = true; jsTx.set(documentRef.jsDoc, data.toJsObject(), o) }
        else { jsTx.set(documentRef.jsDoc, data.toJsObject()) }
        return this
    }
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): Transaction { jsTx.update(documentRef.jsDoc, data.toJsObject()); return this }
    actual fun delete(documentRef: DocumentReference): Transaction { jsTx.delete(documentRef.jsDoc); return this }
}

actual class WriteBatch internal constructor(private val jsBatch: dynamic) {
    actual fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions): WriteBatch {
        if (options.merge) { val o = js("{}"); o.merge = true; jsBatch.set(documentRef.jsDoc, data.toJsObject(), o) }
        else { jsBatch.set(documentRef.jsDoc, data.toJsObject()) }
        return this
    }
    actual fun update(documentRef: DocumentReference, data: Map<String, Any?>): WriteBatch { jsBatch.update(documentRef.jsDoc, data.toJsObject()); return this }
    actual fun delete(documentRef: DocumentReference): WriteBatch { jsBatch.delete(documentRef.jsDoc); return this }
    actual suspend fun commit() { jsBatch.commit().await() }
}

actual fun interface EventListener<T> { actual fun onEvent(value: T?, error: FirestoreException?) }
actual interface ListenerRegistration { actual fun remove() }
actual class FirestoreException internal constructor(message: String, actual val code: Int) : Exception(message)

private fun Map<String, Any?>.toJsObject(): dynamic {
    val obj = js("{}")
    this.forEach { (k, v) -> obj[k] = when (v) { is Map<*, *> -> (v as Map<String, Any?>).toJsObject(); is List<*> -> v.toTypedArray(); else -> v } }
    return obj
}

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}

private fun <T> Promise<T>.then(onFulfilled: (T) -> Unit, onRejected: (Throwable) -> Unit): Promise<T> {
    asDynamic().then(onFulfilled, onRejected)
    return this
}
