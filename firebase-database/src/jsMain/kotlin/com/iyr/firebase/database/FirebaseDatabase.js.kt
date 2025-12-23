package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class FirebaseDatabase internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(js("{}"))
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(js("{}"))
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(js("{}"))
    }
    actual fun getReference(): DatabaseReference = DatabaseReference(js)
    actual fun getReference(path: String): DatabaseReference = DatabaseReference(js)
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference(js)
    actual fun goOnline() {}
    actual fun goOffline() {}
    actual fun setPersistenceEnabled(enabled: Boolean) {}
    actual fun setLogLevel(level: Int) {}
}

actual open class Query internal constructor(open val js: dynamic) {
    actual fun orderByChild(path: String): Query = Query(js)
    actual fun orderByKey(): Query = Query(js)
    actual fun orderByValue(): Query = Query(js)
    actual fun orderByPriority(): Query = Query(js)
    actual fun startAt(value: String?): Query = Query(js)
    actual fun startAt(value: Double): Query = Query(js)
    actual fun startAt(value: Boolean): Query = Query(js)
    actual fun startAt(value: String?, key: String?): Query = Query(js)
    actual fun endAt(value: String?): Query = Query(js)
    actual fun endAt(value: Double): Query = Query(js)
    actual fun endAt(value: Boolean): Query = Query(js)
    actual fun endAt(value: String?, key: String?): Query = Query(js)
    actual fun equalTo(value: String?): Query = Query(js)
    actual fun equalTo(value: Double): Query = Query(js)
    actual fun equalTo(value: Boolean): Query = Query(js)
    actual fun equalTo(value: String?, key: String?): Query = Query(js)
    actual fun limitToFirst(limit: Int): Query = Query(js)
    actual fun limitToLast(limit: Int): Query = Query(js)
    actual suspend fun get(): DataSnapshot = DataSnapshot(js)
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener = listener
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener = listener
    actual fun removeEventListener(listener: ValueEventListener) {}
    actual fun removeEventListener(listener: ChildEventListener) {}
    actual val valueEvents: Flow<DataSnapshot> = MutableStateFlow(DataSnapshot(js))
}

actual class DatabaseReference internal constructor(override val js: dynamic) : Query(js) {
    actual val key: String? get() = js.key as? String
    actual val parent: DatabaseReference? get() = null
    actual val root: DatabaseReference get() = this
    actual fun child(path: String): DatabaseReference = DatabaseReference(js)
    actual fun push(): DatabaseReference = DatabaseReference(js)
    actual suspend fun setValue(value: Any?) {}
    actual suspend fun setValue(value: Any?, priority: Any?) {}
    actual suspend fun updateChildren(update: Map<String, Any?>) {}
    actual suspend fun removeValue() {}
    actual suspend fun setPriority(priority: Any?) {}
    actual fun onDisconnect(): OnDisconnect = OnDisconnect()
}

actual class DataSnapshot internal constructor(val js: dynamic) {
    actual val key: String? get() = js.key as? String
    actual val ref: DatabaseReference get() = DatabaseReference(js)
    actual fun exists(): Boolean = false
    actual fun hasChildren(): Boolean = false
    actual fun hasChild(path: String): Boolean = false
    actual val childrenCount: Long get() = 0
    actual fun child(path: String): DataSnapshot = DataSnapshot(js)
    actual val children: Iterable<DataSnapshot> get() = emptyList()
    actual fun getValue(): Any? = js.`val`?.invoke()
    actual val priority: Any? get() = null
}

actual class OnDisconnect {
    actual suspend fun setValue(value: Any?) {}
    actual suspend fun updateChildren(update: Map<String, Any?>) {}
    actual suspend fun removeValue() {}
    actual suspend fun cancel() {}
}

actual class DatabaseError(actual val message: String) {
    actual val code: Int get() = 0
    actual val details: String? get() = null
    actual fun toException(): Exception = Exception(message)
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
