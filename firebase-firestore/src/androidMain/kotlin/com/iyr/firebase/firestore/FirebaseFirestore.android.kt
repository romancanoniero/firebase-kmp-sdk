package com.iyr.firebase.firestore

import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore as AndroidFirestore
import com.google.firebase.firestore.CollectionReference as AndroidCollection
import com.google.firebase.firestore.DocumentReference as AndroidDocument
import com.google.firebase.firestore.DocumentSnapshot as AndroidDocSnapshot
import com.google.firebase.firestore.QuerySnapshot as AndroidQuerySnapshot
import com.google.firebase.firestore.DocumentChange as AndroidDocChange
import com.google.firebase.firestore.Query as AndroidQuery
import com.google.firebase.firestore.Transaction as AndroidTransaction
import com.google.firebase.firestore.WriteBatch as AndroidBatch
import com.google.firebase.firestore.SetOptions as AndroidSetOptions
import com.google.firebase.firestore.SnapshotMetadata as AndroidMetadata
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseFirestore internal constructor(val android: AndroidFirestore) {
    actual companion object {
        actual fun getInstance(): FirebaseFirestore = FirebaseFirestore(AndroidFirestore.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseFirestore = FirebaseFirestore(AndroidFirestore.getInstance(app.android))
    }
    actual fun collection(collectionPath: String): CollectionReference = CollectionReference(android.collection(collectionPath))
    actual fun document(documentPath: String): DocumentReference = DocumentReference(android.document(documentPath))
    actual fun collectionGroup(collectionId: String): Query = Query(android.collectionGroup(collectionId))
    actual suspend fun <T> runTransaction(block: suspend Transaction.() -> T): T = 
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            android.runTransaction<T> { t -> 
                kotlinx.coroutines.runBlocking { Transaction(t).block() }
            }.await()
        }
    actual fun batch(): WriteBatch = WriteBatch(android.batch())
    actual suspend fun clearPersistence() { android.clearPersistence().await() }
    actual suspend fun enableNetwork() { android.enableNetwork().await() }
    actual suspend fun disableNetwork() { android.disableNetwork().await() }
    actual suspend fun terminate() { android.terminate().await() }
    actual suspend fun waitForPendingWrites() { android.waitForPendingWrites().await() }
    actual fun useEmulator(host: String, port: Int) { android.useEmulator(host, port) }
}

actual open class Query internal constructor(open val android: AndroidQuery) {
    actual fun whereEqualTo(field: String, value: Any?): Query = Query(android.whereEqualTo(field, value))
    actual fun whereNotEqualTo(field: String, value: Any?): Query = Query(android.whereNotEqualTo(field, value))
    actual fun whereLessThan(field: String, value: Any): Query = Query(android.whereLessThan(field, value))
    actual fun whereLessThanOrEqualTo(field: String, value: Any): Query = Query(android.whereLessThanOrEqualTo(field, value))
    actual fun whereGreaterThan(field: String, value: Any): Query = Query(android.whereGreaterThan(field, value))
    actual fun whereGreaterThanOrEqualTo(field: String, value: Any): Query = Query(android.whereGreaterThanOrEqualTo(field, value))
    actual fun whereArrayContains(field: String, value: Any): Query = Query(android.whereArrayContains(field, value))
    actual fun whereArrayContainsAny(field: String, values: List<Any>): Query = Query(android.whereArrayContainsAny(field, values))
    actual fun whereIn(field: String, values: List<Any>): Query = Query(android.whereIn(field, values))
    actual fun whereNotIn(field: String, values: List<Any>): Query = Query(android.whereNotIn(field, values))
    actual fun orderBy(field: String, direction: Direction): Query = Query(android.orderBy(field, direction.toAndroid()))
    actual fun limit(limit: Long): Query = Query(android.limit(limit))
    actual fun limitToLast(limit: Long): Query = Query(android.limitToLast(limit))
    actual fun startAt(vararg fieldValues: Any): Query = Query(android.startAt(*fieldValues))
    actual fun startAfter(vararg fieldValues: Any): Query = Query(android.startAfter(*fieldValues))
    actual fun endAt(vararg fieldValues: Any): Query = Query(android.endAt(*fieldValues))
    actual fun endBefore(vararg fieldValues: Any): Query = Query(android.endBefore(*fieldValues))
    actual suspend fun get(): QuerySnapshot = QuerySnapshot(android.get().await())
    actual fun addSnapshotListener(listener: EventListener<QuerySnapshot>): ListenerRegistration {
        val reg = android.addSnapshotListener { s, e -> listener.onEvent(s?.let { QuerySnapshot(it) }, e?.let { FirestoreException(it) }) }
        return ListenerRegistration { reg.remove() }
    }
    actual val snapshots: Flow<QuerySnapshot> = callbackFlow {
        val reg = android.addSnapshotListener { s, e -> if (e != null) close(e) else s?.let { trySend(QuerySnapshot(it)) } }
        awaitClose { reg.remove() }
    }
}

private fun Direction.toAndroid() = when (this) {
    Direction.ASCENDING -> AndroidQuery.Direction.ASCENDING
    Direction.DESCENDING -> AndroidQuery.Direction.DESCENDING
}
