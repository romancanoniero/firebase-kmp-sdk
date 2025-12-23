package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import com.google.firebase.database.FirebaseDatabase as AndroidDatabase
import com.google.firebase.database.DatabaseReference as AndroidRef
import com.google.firebase.database.DataSnapshot as AndroidSnapshot
import com.google.firebase.database.DatabaseError as AndroidError
import com.google.firebase.database.Query as AndroidQuery
import com.google.firebase.database.ValueEventListener as AndroidValueListener
import com.google.firebase.database.ChildEventListener as AndroidChildListener
import com.google.firebase.database.OnDisconnect as AndroidOnDisconnect
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseDatabase internal constructor(val android: AndroidDatabase) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(AndroidDatabase.getInstance())
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(AndroidDatabase.getInstance(app.android))
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(AndroidDatabase.getInstance(url))
    }
    actual fun getReference(): DatabaseReference = DatabaseReference(android.reference)
    actual fun getReference(path: String): DatabaseReference = DatabaseReference(android.getReference(path))
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference(android.getReferenceFromUrl(url))
    actual fun goOnline() = android.goOnline()
    actual fun goOffline() = android.goOffline()
    actual fun setPersistenceEnabled(enabled: Boolean) { android.setPersistenceEnabled(enabled) }
    actual fun setLogLevel(level: Int) { /* Android handles differently */ }
}

actual open class Query internal constructor(open val android: AndroidQuery) {
    actual fun orderByChild(path: String): Query = Query(android.orderByChild(path))
    actual fun orderByKey(): Query = Query(android.orderByKey())
    actual fun orderByValue(): Query = Query(android.orderByValue())
    actual fun orderByPriority(): Query = Query(android.orderByPriority())
    actual fun startAt(value: String?): Query = Query(android.startAt(value))
    actual fun startAt(value: Double): Query = Query(android.startAt(value))
    actual fun startAt(value: Boolean): Query = Query(android.startAt(value))
    actual fun startAt(value: String?, key: String?): Query = Query(android.startAt(value, key))
    actual fun endAt(value: String?): Query = Query(android.endAt(value))
    actual fun endAt(value: Double): Query = Query(android.endAt(value))
    actual fun endAt(value: Boolean): Query = Query(android.endAt(value))
    actual fun endAt(value: String?, key: String?): Query = Query(android.endAt(value, key))
    actual fun equalTo(value: String?): Query = Query(android.equalTo(value))
    actual fun equalTo(value: Double): Query = Query(android.equalTo(value))
    actual fun equalTo(value: Boolean): Query = Query(android.equalTo(value))
    actual fun equalTo(value: String?, key: String?): Query = Query(android.equalTo(value, key))
    actual fun limitToFirst(limit: Int): Query = Query(android.limitToFirst(limit))
    actual fun limitToLast(limit: Int): Query = Query(android.limitToLast(limit))
    actual suspend fun get(): DataSnapshot = DataSnapshot(android.get().await())
    
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener {
        android.addValueEventListener(listener.toAndroid())
        return listener
    }
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener {
        android.addChildEventListener(listener.toAndroid())
        return listener
    }
    actual fun removeEventListener(listener: ValueEventListener) { android.removeEventListener(listener.toAndroid()) }
    actual fun removeEventListener(listener: ChildEventListener) { android.removeEventListener(listener.toAndroid()) }
    
    actual val valueEvents: Flow<DataSnapshot> = callbackFlow {
        val l = object : AndroidValueListener {
            override fun onDataChange(s: AndroidSnapshot) { trySend(DataSnapshot(s)) }
            override fun onCancelled(e: AndroidError) { close(e.toException()) }
        }
        android.addValueEventListener(l)
        awaitClose { android.removeEventListener(l) }
    }
}

private fun ValueEventListener.toAndroid() = object : AndroidValueListener {
    override fun onDataChange(s: AndroidSnapshot) { this@toAndroid.onDataChange(DataSnapshot(s)) }
    override fun onCancelled(e: AndroidError) { this@toAndroid.onCancelled(DatabaseError(e)) }
}

private fun ChildEventListener.toAndroid() = object : AndroidChildListener {
    override fun onChildAdded(s: AndroidSnapshot, p: String?) { this@toAndroid.onChildAdded(DataSnapshot(s), p) }
    override fun onChildChanged(s: AndroidSnapshot, p: String?) { this@toAndroid.onChildChanged(DataSnapshot(s), p) }
    override fun onChildRemoved(s: AndroidSnapshot) { this@toAndroid.onChildRemoved(DataSnapshot(s)) }
    override fun onChildMoved(s: AndroidSnapshot, p: String?) { this@toAndroid.onChildMoved(DataSnapshot(s), p) }
    override fun onCancelled(e: AndroidError) { this@toAndroid.onCancelled(DatabaseError(e)) }
}
