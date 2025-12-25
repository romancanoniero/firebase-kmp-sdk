package com.iyr.firebase.firestore

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseFirestoreInternal.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseFirestore internal constructor(val ios: FIRFirestore) {
    actual companion object {
        actual fun getInstance(): FirebaseFirestore = FirebaseFirestore(FIRFirestore.firestore())
        
        actual fun getInstance(app: FirebaseApp): FirebaseFirestore {
            val appName = app.getName()
            return if (appName == "[DEFAULT]") {
                FirebaseFirestore(FIRFirestore.firestore())
            } else {
                throw UnsupportedOperationException(
                    "Para apps con nombre custom en iOS, usa getInstance() después de configurar la app"
                )
            }
        }
    }
    
    actual fun collection(collectionPath: String): CollectionReference = 
        CollectionReference(ios.collectionWithPath(collectionPath))
    
    actual fun document(documentPath: String): DocumentReference = 
        DocumentReference(ios.documentWithPath(documentPath))
    
    actual fun collectionGroup(collectionId: String): Query = 
        Query(ios.collectionGroupWithID(collectionId))
    
    actual suspend fun <T> runTransaction(block: suspend Transaction.() -> T): T = 
        throw UnsupportedOperationException("runTransaction not yet implemented for iOS")
    
    actual fun batch(): WriteBatch = WriteBatch(ios.batch())
    
    actual suspend fun clearPersistence(): Unit = suspendCancellableCoroutine { cont ->
        ios.clearPersistenceWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun enableNetwork(): Unit = suspendCancellableCoroutine { cont ->
        ios.enableNetworkWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun disableNetwork(): Unit = suspendCancellableCoroutine { cont ->
        ios.disableNetworkWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun terminate(): Unit = suspendCancellableCoroutine { cont ->
        ios.terminateWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual suspend fun waitForPendingWrites(): Unit = suspendCancellableCoroutine { cont ->
        ios.waitForPendingWritesWithCompletion { error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(Unit)
        }
    }
    
    actual fun useEmulator(host: String, port: Int) { 
        ios.useEmulatorWithHost(host, port.toLong()) 
    }
}

actual open class Query internal constructor(open val ios: FIRQuery) {
    actual fun whereEqualTo(field: String, value: Any?): Query = 
        Query(ios.queryWhereField(field, isEqualTo = value ?: NSNull.`null`()))
    
    actual fun whereNotEqualTo(field: String, value: Any?): Query = 
        Query(ios.queryWhereField(field, isNotEqualTo = value ?: NSNull.`null`()))
    
    actual fun whereLessThan(field: String, value: Any): Query = 
        Query(ios.queryWhereField(field, isLessThan = value))
    
    actual fun whereLessThanOrEqualTo(field: String, value: Any): Query = 
        Query(ios.queryWhereField(field, isLessThanOrEqualTo = value))
    
    actual fun whereGreaterThan(field: String, value: Any): Query = 
        Query(ios.queryWhereField(field, isGreaterThan = value))
    
    actual fun whereGreaterThanOrEqualTo(field: String, value: Any): Query = 
        Query(ios.queryWhereField(field, isGreaterThanOrEqualTo = value))
    
    actual fun whereArrayContains(field: String, value: Any): Query = 
        Query(ios.queryWhereField(field, arrayContains = value))
    
    actual fun whereArrayContainsAny(field: String, values: List<Any>): Query = 
        Query(ios.queryWhereField(field, arrayContainsAny = values))
    
    actual fun whereIn(field: String, values: List<Any>): Query = 
        Query(ios.queryWhereField(field, `in` = values))
    
    actual fun whereNotIn(field: String, values: List<Any>): Query = 
        Query(ios.queryWhereField(field, notIn = values))
    
    actual fun orderBy(field: String, direction: Direction): Query = 
        Query(ios.queryOrderedByField(field, descending = direction == Direction.DESCENDING))
    
    actual fun limit(limit: Long): Query = Query(ios.queryLimitedTo(limit))
    
    actual fun limitToLast(limit: Long): Query = Query(ios.queryLimitedToLast(limit))
    
    actual fun startAt(vararg fieldValues: Any): Query = 
        Query(ios.queryStartingAtValues(fieldValues.toList()))
    
    actual fun startAfter(vararg fieldValues: Any): Query = 
        Query(ios.queryStartingAfterValues(fieldValues.toList()))
    
    actual fun endAt(vararg fieldValues: Any): Query = 
        Query(ios.queryEndingAtValues(fieldValues.toList()))
    
    actual fun endBefore(vararg fieldValues: Any): Query = 
        Query(ios.queryEndingBeforeValues(fieldValues.toList()))
    
    actual suspend fun get(): QuerySnapshot = suspendCancellableCoroutine { cont ->
        ios.getDocumentsWithCompletion { snapshot, error ->
            if (error != null) cont.resumeWithException(error.toException())
            else cont.resume(QuerySnapshot(snapshot!!))
        }
    }
    
    actual fun addSnapshotListener(listener: EventListener<QuerySnapshot>): ListenerRegistration {
        val handle = ios.addSnapshotListener { snapshot, error ->
            listener.onEvent(
                snapshot?.let { QuerySnapshot(it) }, 
                error?.let { FirestoreException(it.localizedDescription, 0) }
            )
        }
        return ListenerRegistration { handle?.remove() }
    }
    
    actual val snapshots: Flow<QuerySnapshot> = callbackFlow {
        val handle = ios.addSnapshotListener { snapshot, error ->
            if (error != null) close(error.toException())
            else snapshot?.let { trySend(QuerySnapshot(it)) }
        }
        awaitClose { handle?.remove() }
    }
}

private fun NSError.toException() = Exception(localizedDescription)
