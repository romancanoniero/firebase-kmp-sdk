package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class FirebaseDatabase internal constructor(private val jsDb: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(null)
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(null)
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(null)
    }
    actual fun getReference(): DatabaseReference = DatabaseReference()
    actual fun getReference(path: String): DatabaseReference = DatabaseReference()
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference()
    actual fun goOnline() {}
    actual fun goOffline() {}
    actual fun setPersistenceEnabled(enabled: Boolean) {}
    actual fun setLogLevel(level: Int) {}
}

actual open class Query {
    actual fun orderByChild(path: String): Query = Query()
    actual fun orderByKey(): Query = Query()
    actual fun orderByValue(): Query = Query()
    actual fun orderByPriority(): Query = Query()
    actual fun startAt(value: String?): Query = Query()
    actual fun startAt(value: Double): Query = Query()
    actual fun startAt(value: Boolean): Query = Query()
    actual fun startAt(value: String?, key: String?): Query = Query()
    actual fun endAt(value: String?): Query = Query()
    actual fun endAt(value: Double): Query = Query()
    actual fun endAt(value: Boolean): Query = Query()
    actual fun endAt(value: String?, key: String?): Query = Query()
    actual fun equalTo(value: String?): Query = Query()
    actual fun equalTo(value: Double): Query = Query()
    actual fun equalTo(value: Boolean): Query = Query()
    actual fun equalTo(value: String?, key: String?): Query = Query()
    actual fun limitToFirst(limit: Int): Query = Query()
    actual fun limitToLast(limit: Int): Query = Query()
    actual suspend fun get(): DataSnapshot = DataSnapshot()
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener = listener
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener = listener
    actual fun removeEventListener(listener: ValueEventListener) {}
    actual fun removeEventListener(listener: ChildEventListener) {}
    actual val valueEvents: Flow<DataSnapshot> = MutableStateFlow(DataSnapshot())
}

actual class DatabaseReference : Query() {
    actual val key: String? get() = null
    actual val parent: DatabaseReference? get() = null
    actual val root: DatabaseReference get() = DatabaseReference()
    actual fun child(path: String): DatabaseReference = DatabaseReference()
    actual fun push(): DatabaseReference = DatabaseReference()
    actual suspend fun setValue(value: Any?) {}
    actual suspend fun setValue(value: Any?, priority: Any?) {}
    actual suspend fun updateChildren(update: Map<String, Any?>) {}
    actual suspend fun removeValue() {}
    actual suspend fun setPriority(priority: Any?) {}
    actual fun onDisconnect(): OnDisconnect = OnDisconnect()
}

actual class DataSnapshot {
    actual val key: String? get() = null
    actual val ref: DatabaseReference get() = DatabaseReference()
    actual fun exists(): Boolean = false
    actual fun hasChildren(): Boolean = false
    actual fun hasChild(path: String): Boolean = false
    actual val childrenCount: Long get() = 0
    actual fun child(path: String): DataSnapshot = DataSnapshot()
    actual val children: Iterable<DataSnapshot> get() = emptyList()
    actual fun getValue(): Any? = null
    actual val priority: Any? get() = null
}

actual class OnDisconnect {
    actual suspend fun setValue(value: Any?) {}
    actual suspend fun updateChildren(update: Map<String, Any?>) {}
    actual suspend fun removeValue() {}
    actual suspend fun cancel() {}
}

actual class DatabaseError(private val msg: String) {
    actual val code: Int get() = 0
    actual val message: String get() = msg
    actual val details: String? get() = null
    actual fun toException(): Exception = Exception(msg)
}

actual interface ValueEventListener {
    actual fun onDataChange(snapshot: DataSnapshot)
    actual fun onCancelled(error: DatabaseError)
}

actual interface ChildEventListener {
    actual fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?)
    actual fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?)
    actual fun onChildRemoved(snapshot: DataSnapshot)
    actual fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?)
    actual fun onCancelled(error: DatabaseError)
}

actual class DatabaseException : Exception()
