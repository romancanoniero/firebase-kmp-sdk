package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.Promise

// Firebase Database JS SDK external declarations
@JsModule("firebase/database")
@JsNonModule
external object FirebaseDatabaseJS {
    fun getDatabase(app: dynamic = definedExternally): dynamic
    fun ref(db: dynamic, path: String = definedExternally): dynamic
    fun get(query: dynamic): Promise<dynamic>
    fun set(ref: dynamic, value: dynamic): Promise<dynamic>
    fun update(ref: dynamic, values: dynamic): Promise<dynamic>
    fun remove(ref: dynamic): Promise<dynamic>
    fun push(ref: dynamic): dynamic
    fun child(ref: dynamic, path: String): dynamic
    fun onValue(query: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onChildAdded(query: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun onChildChanged(query: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun onChildRemoved(query: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onChildMoved(query: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun off(query: dynamic): Unit
    fun query(ref: dynamic, vararg constraints: dynamic): dynamic
    fun orderByChild(path: String): dynamic
    fun orderByKey(): dynamic
    fun orderByValue(): dynamic
    fun orderByPriority(): dynamic
    fun startAt(value: dynamic, key: String? = definedExternally): dynamic
    fun endAt(value: dynamic, key: String? = definedExternally): dynamic
    fun equalTo(value: dynamic, key: String? = definedExternally): dynamic
    fun limitToFirst(limit: Int): dynamic
    fun limitToLast(limit: Int): dynamic
    fun goOnline(db: dynamic): Unit
    fun goOffline(db: dynamic): Unit
}

actual class FirebaseDatabase internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(FirebaseDatabaseJS.getDatabase())
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(FirebaseDatabaseJS.getDatabase(app.js))
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(FirebaseDatabaseJS.getDatabase(js("{ databaseURL: '$url' }")))
    }
    
    actual fun getReference(): DatabaseReference = DatabaseReference(FirebaseDatabaseJS.ref(js))
    actual fun getReference(path: String): DatabaseReference = DatabaseReference(FirebaseDatabaseJS.ref(js, path))
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference(FirebaseDatabaseJS.ref(js, url))
    actual fun goOnline() { FirebaseDatabaseJS.goOnline(js) }
    actual fun goOffline() { FirebaseDatabaseJS.goOffline(js) }
    actual fun setPersistenceEnabled(enabled: Boolean) { /* Not available in JS */ }
    actual fun setLogLevel(level: Int) { /* Not available in JS */ }
}

actual open class Query internal constructor(open val js: dynamic) {
    actual fun orderByChild(path: String): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.orderByChild(path)))
    actual fun orderByKey(): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.orderByKey()))
    actual fun orderByValue(): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.orderByValue()))
    actual fun orderByPriority(): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.orderByPriority()))
    actual fun startAt(value: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.startAt(value)))
    actual fun startAt(value: Double): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.startAt(value)))
    actual fun startAt(value: Boolean): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.startAt(value)))
    actual fun startAt(value: String?, key: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.startAt(value, key)))
    actual fun endAt(value: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.endAt(value)))
    actual fun endAt(value: Double): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.endAt(value)))
    actual fun endAt(value: Boolean): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.endAt(value)))
    actual fun endAt(value: String?, key: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.endAt(value, key)))
    actual fun equalTo(value: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.equalTo(value)))
    actual fun equalTo(value: Double): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.equalTo(value)))
    actual fun equalTo(value: Boolean): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.equalTo(value)))
    actual fun equalTo(value: String?, key: String?): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.equalTo(value, key)))
    actual fun limitToFirst(limit: Int): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.limitToFirst(limit)))
    actual fun limitToLast(limit: Int): Query = Query(FirebaseDatabaseJS.query(js, FirebaseDatabaseJS.limitToLast(limit)))
    
    actual suspend fun get(): DataSnapshot {
        val snapshot = FirebaseDatabaseJS.get(js).await()
        return DataSnapshot(snapshot)
    }
    
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener {
        FirebaseDatabaseJS.onValue(js) { snapshot ->
            listener.onDataChange(DataSnapshot(snapshot))
        }
        return listener
    }
    
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener {
        FirebaseDatabaseJS.onChildAdded(js) { snapshot, prev -> listener.onChildAdded(DataSnapshot(snapshot), prev) }
        FirebaseDatabaseJS.onChildChanged(js) { snapshot, prev -> listener.onChildChanged(DataSnapshot(snapshot), prev) }
        FirebaseDatabaseJS.onChildRemoved(js) { snapshot -> listener.onChildRemoved(DataSnapshot(snapshot)) }
        FirebaseDatabaseJS.onChildMoved(js) { snapshot, prev -> listener.onChildMoved(DataSnapshot(snapshot), prev) }
        return listener
    }
    
    actual fun removeEventListener(listener: ValueEventListener) { FirebaseDatabaseJS.off(js) }
    actual fun removeEventListener(listener: ChildEventListener) { FirebaseDatabaseJS.off(js) }
    
    actual val valueEvents: Flow<DataSnapshot> = callbackFlow {
        val unsubscribe = FirebaseDatabaseJS.onValue(js) { snapshot ->
            trySend(DataSnapshot(snapshot))
        }
        awaitClose { unsubscribe() }
    }
}

actual class DatabaseReference internal constructor(override val js: dynamic) : Query(js) {
    actual val key: String? get() = js.key as? String
    actual val parent: DatabaseReference? get() = js.parent?.let { DatabaseReference(it) }
    actual val root: DatabaseReference get() = DatabaseReference(js.root)
    actual fun child(path: String): DatabaseReference = DatabaseReference(FirebaseDatabaseJS.child(js, path))
    actual fun push(): DatabaseReference = DatabaseReference(FirebaseDatabaseJS.push(js))
    
    actual suspend fun setValue(value: Any?) { FirebaseDatabaseJS.set(js, value).await() }
    actual suspend fun setValue(value: Any?, priority: Any?) { 
        FirebaseDatabaseJS.set(js, js("{ '.value': value, '.priority': priority }")).await()
    }
    actual suspend fun updateChildren(update: Map<String, Any?>) { 
        val jsObject = js("{}")
        update.forEach { (k, v) -> jsObject[k] = v }
        FirebaseDatabaseJS.update(js, jsObject).await() 
    }
    actual suspend fun removeValue() { FirebaseDatabaseJS.remove(js).await() }
    actual suspend fun setPriority(priority: Any?) { /* Set via setValue with priority */ }
    actual fun onDisconnect(): OnDisconnect = OnDisconnect(js.onDisconnect())
}

actual class DataSnapshot internal constructor(val js: dynamic) {
    actual val key: String? get() = js.key as? String
    actual val ref: DatabaseReference get() = DatabaseReference(js.ref)
    actual fun exists(): Boolean = js.exists() as Boolean
    actual fun hasChildren(): Boolean = js.hasChildren() as Boolean
    actual fun hasChild(path: String): Boolean = js.hasChild(path) as Boolean
    actual val childrenCount: Long get() = (js.size as? Number)?.toLong() ?: 0L
    actual fun child(path: String): DataSnapshot = DataSnapshot(js.child(path))
    actual val children: Iterable<DataSnapshot> get() {
        val list = mutableListOf<DataSnapshot>()
        js.forEach { child: dynamic -> list.add(DataSnapshot(child)); false }
        return list
    }
    actual fun getValue(): Any? = js.`val`()
    actual val priority: Any? get() = js.priority
}

actual class OnDisconnect internal constructor(private val js: dynamic) {
    actual suspend fun setValue(value: Any?) { (js.set(value) as Promise<dynamic>).await() }
    actual suspend fun updateChildren(update: Map<String, Any?>) { 
        val jsObject = js("{}")
        update.forEach { (k, v) -> jsObject[k] = v }
        (js.update(jsObject) as Promise<dynamic>).await()
    }
    actual suspend fun removeValue() { (js.remove() as Promise<dynamic>).await() }
    actual suspend fun cancel() { (js.cancel() as Promise<dynamic>).await() }
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

private suspend fun <T> Promise<T>.await(): T = kotlinx.coroutines.await()
