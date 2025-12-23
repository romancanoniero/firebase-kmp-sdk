package com.iyr.firebase.firestore

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow

expect class FirebaseFirestore {
    companion object {
        fun getInstance(): FirebaseFirestore
        fun getInstance(app: FirebaseApp): FirebaseFirestore
    }
    fun collection(collectionPath: String): CollectionReference
    fun document(documentPath: String): DocumentReference
    fun collectionGroup(collectionId: String): Query
    suspend fun <T> runTransaction(block: suspend Transaction.() -> T): T
    fun batch(): WriteBatch
    suspend fun clearPersistence()
    suspend fun enableNetwork()
    suspend fun disableNetwork()
    suspend fun terminate()
    suspend fun waitForPendingWrites()
    fun useEmulator(host: String, port: Int)
}

expect open class Query {
    fun whereEqualTo(field: String, value: Any?): Query
    fun whereNotEqualTo(field: String, value: Any?): Query
    fun whereLessThan(field: String, value: Any): Query
    fun whereLessThanOrEqualTo(field: String, value: Any): Query
    fun whereGreaterThan(field: String, value: Any): Query
    fun whereGreaterThanOrEqualTo(field: String, value: Any): Query
    fun whereArrayContains(field: String, value: Any): Query
    fun whereArrayContainsAny(field: String, values: List<Any>): Query
    fun whereIn(field: String, values: List<Any>): Query
    fun whereNotIn(field: String, values: List<Any>): Query
    fun orderBy(field: String, direction: Direction = Direction.ASCENDING): Query
    fun limit(limit: Long): Query
    fun limitToLast(limit: Long): Query
    fun startAt(vararg fieldValues: Any): Query
    fun startAfter(vararg fieldValues: Any): Query
    fun endAt(vararg fieldValues: Any): Query
    fun endBefore(vararg fieldValues: Any): Query
    suspend fun get(): QuerySnapshot
    fun addSnapshotListener(listener: EventListener<QuerySnapshot>): ListenerRegistration
    val snapshots: Flow<QuerySnapshot>
}

expect class CollectionReference : Query {
    val id: String
    val path: String
    val parent: DocumentReference?
    fun document(): DocumentReference
    fun document(documentPath: String): DocumentReference
    suspend fun add(data: Map<String, Any?>): DocumentReference
}

expect class DocumentReference {
    val id: String
    val path: String
    val parent: CollectionReference
    fun collection(collectionPath: String): CollectionReference
    suspend fun get(): DocumentSnapshot
    suspend fun set(data: Map<String, Any?>, options: SetOptions = SetOptions.overwrite())
    suspend fun update(data: Map<String, Any?>)
    suspend fun delete()
    fun addSnapshotListener(listener: EventListener<DocumentSnapshot>): ListenerRegistration
    val snapshots: Flow<DocumentSnapshot>
}

expect class DocumentSnapshot {
    val id: String
    val reference: DocumentReference
    fun exists(): Boolean
    fun getData(): Map<String, Any?>?
    fun get(field: String): Any?
    fun contains(field: String): Boolean
    val metadata: SnapshotMetadata
}

expect class QuerySnapshot {
    val documents: List<DocumentSnapshot>
    val documentChanges: List<DocumentChange>
    val isEmpty: Boolean
    val size: Int
    val metadata: SnapshotMetadata
}

expect class DocumentChange {
    val type: DocumentChangeType
    val document: DocumentSnapshot
    val oldIndex: Int
    val newIndex: Int
}

expect enum class DocumentChangeType { ADDED, MODIFIED, REMOVED }
expect enum class Direction { ASCENDING, DESCENDING }

expect class SnapshotMetadata {
    val hasPendingWrites: Boolean
    val isFromCache: Boolean
}

expect class SetOptions {
    companion object {
        fun overwrite(): SetOptions
        fun merge(): SetOptions
        fun mergeFields(vararg fields: String): SetOptions
    }
}

expect class Transaction {
    fun get(documentRef: DocumentReference): DocumentSnapshot
    fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions = SetOptions.overwrite()): Transaction
    fun update(documentRef: DocumentReference, data: Map<String, Any?>): Transaction
    fun delete(documentRef: DocumentReference): Transaction
}

expect class WriteBatch {
    fun set(documentRef: DocumentReference, data: Map<String, Any?>, options: SetOptions = SetOptions.overwrite()): WriteBatch
    fun update(documentRef: DocumentReference, data: Map<String, Any?>): WriteBatch
    fun delete(documentRef: DocumentReference): WriteBatch
    suspend fun commit()
}

expect fun interface EventListener<T> { fun onEvent(value: T?, error: FirestoreException?) }
expect interface ListenerRegistration { fun remove() }
expect class FirestoreException : Exception { val code: Int }
