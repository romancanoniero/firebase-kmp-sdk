package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import cocoapods.FirebaseDatabase.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseDatabase internal constructor(val ios: FIRDatabase) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(FIRDatabase.database())
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(FIRDatabase.databaseForApp(app.ios))
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(FIRDatabase.databaseWithURL(url))
    }
    actual fun getReference(): DatabaseReference = DatabaseReference(ios.reference())
    actual fun getReference(path: String): DatabaseReference = DatabaseReference(ios.referenceWithPath(path))
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference(ios.referenceFromURL(url))
    actual fun goOnline() = ios.goOnline()
    actual fun goOffline() = ios.goOffline()
    actual fun setPersistenceEnabled(enabled: Boolean) { ios.setPersistenceEnabled(enabled) }
    actual fun setLogLevel(level: Int) {}
}

actual open class Query internal constructor(open val ios: FIRDatabaseQuery) {
    actual fun orderByChild(path: String): Query = Query(ios.queryOrderedByChild(path))
    actual fun orderByKey(): Query = Query(ios.queryOrderedByKey())
    actual fun orderByValue(): Query = Query(ios.queryOrderedByValue())
    actual fun orderByPriority(): Query = Query(ios.queryOrderedByPriority())
    actual fun startAt(value: String?): Query = Query(ios.queryStartingAtValue(value))
    actual fun startAt(value: Double): Query = Query(ios.queryStartingAtValue(value))
    actual fun startAt(value: Boolean): Query = Query(ios.queryStartingAtValue(value))
    actual fun startAt(value: String?, key: String?): Query = Query(ios.queryStartingAtValue(value, childKey = key))
    actual fun endAt(value: String?): Query = Query(ios.queryEndingAtValue(value))
    actual fun endAt(value: Double): Query = Query(ios.queryEndingAtValue(value))
    actual fun endAt(value: Boolean): Query = Query(ios.queryEndingAtValue(value))
    actual fun endAt(value: String?, key: String?): Query = Query(ios.queryEndingAtValue(value, childKey = key))
    actual fun equalTo(value: String?): Query = Query(ios.queryEqualToValue(value))
    actual fun equalTo(value: Double): Query = Query(ios.queryEqualToValue(value))
    actual fun equalTo(value: Boolean): Query = Query(ios.queryEqualToValue(value))
    actual fun equalTo(value: String?, key: String?): Query = Query(ios.queryEqualToValue(value, childKey = key))
    actual fun limitToFirst(limit: Int): Query = Query(ios.queryLimitedToFirst(limit.toULong()))
    actual fun limitToLast(limit: Int): Query = Query(ios.queryLimitedToLast(limit.toULong()))
    
    actual suspend fun get(): DataSnapshot = suspendCancellableCoroutine { cont ->
        ios.getDataWithCompletionBlock { error, snapshot ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(DataSnapshot(snapshot!!))
        }
    }
    
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener {
        ios.observeEventType(FIRDataEventTypeValue) { snapshot, _ ->
            snapshot?.let { listener.onDataChange(DataSnapshot(it)) }
        }
        return listener
    }
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener = listener
    actual fun removeEventListener(listener: ValueEventListener) {}
    actual fun removeEventListener(listener: ChildEventListener) {}
    
    actual val valueEvents: Flow<DataSnapshot> = callbackFlow {
        val handle = ios.observeEventType(FIRDataEventTypeValue) { snapshot, _ ->
            snapshot?.let { trySend(DataSnapshot(it)) }
        }
        awaitClose { ios.removeObserverWithHandle(handle) }
    }
}
