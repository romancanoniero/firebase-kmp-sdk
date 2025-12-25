@file:Suppress("UNCHECKED_CAST")
package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.js.Promise

@JsModule("firebase/database")
@JsNonModule
private external object DatabaseModule {
    fun getDatabase(): dynamic
    fun getDatabase(app: dynamic): dynamic
    fun getDatabase(app: dynamic, url: String): dynamic
    fun ref(db: dynamic, path: String?): dynamic
    fun child(ref: dynamic, path: String): dynamic
    fun push(ref: dynamic): dynamic
    fun set(ref: dynamic, value: Any?): Promise<dynamic>
    fun update(ref: dynamic, values: dynamic): Promise<dynamic>
    fun remove(ref: dynamic): Promise<dynamic>
    fun get(ref: dynamic): Promise<dynamic>
    fun onValue(ref: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onChildAdded(ref: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun onChildChanged(ref: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun onChildRemoved(ref: dynamic, callback: (dynamic) -> Unit): () -> Unit
    fun onChildMoved(ref: dynamic, callback: (dynamic, String?) -> Unit): () -> Unit
    fun off(ref: dynamic)
    fun query(ref: dynamic, vararg constraints: dynamic): dynamic
    fun orderByChild(path: String): dynamic
    fun orderByKey(): dynamic
    fun orderByValue(): dynamic
    fun orderByPriority(): dynamic
    fun startAt(value: Any?, key: String?): dynamic
    fun endAt(value: Any?, key: String?): dynamic
    fun equalTo(value: Any?, key: String?): dynamic
    fun limitToFirst(limit: Int): dynamic
    fun limitToLast(limit: Int): dynamic
    fun goOffline(db: dynamic)
    fun goOnline(db: dynamic)
    fun onDisconnect(ref: dynamic): dynamic
}

actual class FirebaseDatabase internal constructor(internal val jsDb: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseDatabase = FirebaseDatabase(DatabaseModule.getDatabase())
        actual fun getInstance(app: FirebaseApp): FirebaseDatabase = FirebaseDatabase(DatabaseModule.getDatabase(app.js))
        actual fun getInstance(url: String): FirebaseDatabase = FirebaseDatabase(DatabaseModule.getDatabase(null, url))
    }
    
    actual fun getReference(): DatabaseReference = DatabaseReference(DatabaseModule.ref(jsDb, null))
    actual fun getReference(path: String): DatabaseReference = DatabaseReference(DatabaseModule.ref(jsDb, path))
    actual fun getReferenceFromUrl(url: String): DatabaseReference = DatabaseReference(DatabaseModule.ref(jsDb, url))
    actual fun goOnline() { DatabaseModule.goOnline(jsDb) }
    actual fun goOffline() { DatabaseModule.goOffline(jsDb) }
    actual fun setPersistenceEnabled(enabled: Boolean) {}
    actual fun setLogLevel(level: Int) {}
}

actual open class Query internal constructor(internal open val jsRef: dynamic) {
    actual fun orderByChild(path: String): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.orderByChild(path)))
    actual fun orderByKey(): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.orderByKey()))
    actual fun orderByValue(): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.orderByValue()))
    actual fun orderByPriority(): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.orderByPriority()))
    
    actual fun startAt(value: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.startAt(value, null)))
    actual fun startAt(value: Double): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.startAt(value, null)))
    actual fun startAt(value: Boolean): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.startAt(value, null)))
    actual fun startAt(value: String?, key: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.startAt(value, key)))
    
    actual fun endAt(value: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.endAt(value, null)))
    actual fun endAt(value: Double): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.endAt(value, null)))
    actual fun endAt(value: Boolean): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.endAt(value, null)))
    actual fun endAt(value: String?, key: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.endAt(value, key)))
    
    actual fun equalTo(value: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.equalTo(value, null)))
    actual fun equalTo(value: Double): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.equalTo(value, null)))
    actual fun equalTo(value: Boolean): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.equalTo(value, null)))
    actual fun equalTo(value: String?, key: String?): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.equalTo(value, key)))
    
    actual fun limitToFirst(limit: Int): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.limitToFirst(limit)))
    actual fun limitToLast(limit: Int): Query = Query(DatabaseModule.query(jsRef, DatabaseModule.limitToLast(limit)))
    
    actual suspend fun get(): DataSnapshot {
        val result = DatabaseModule.get(jsRef).await<dynamic>()
        return DataSnapshot(result)
    }
    
    actual fun addValueEventListener(listener: ValueEventListener): ValueEventListener {
        DatabaseModule.onValue(jsRef) { snap -> listener.onDataChange(DataSnapshot(snap)) }
        return listener
    }
    
    actual fun addChildEventListener(listener: ChildEventListener): ChildEventListener {
        DatabaseModule.onChildAdded(jsRef) { snap, prev -> listener.onChildAdded(DataSnapshot(snap), prev) }
        DatabaseModule.onChildChanged(jsRef) { snap, prev -> listener.onChildChanged(DataSnapshot(snap), prev) }
        DatabaseModule.onChildRemoved(jsRef) { snap -> listener.onChildRemoved(DataSnapshot(snap)) }
        DatabaseModule.onChildMoved(jsRef) { snap, prev -> listener.onChildMoved(DataSnapshot(snap), prev) }
        return listener
    }
    
    actual fun removeEventListener(listener: ValueEventListener) { DatabaseModule.off(jsRef) }
    actual fun removeEventListener(listener: ChildEventListener) { DatabaseModule.off(jsRef) }
    
    actual val valueEvents: Flow<DataSnapshot> = callbackFlow {
        val unsub = DatabaseModule.onValue(jsRef) { snap -> trySend(DataSnapshot(snap)) }
        awaitClose { unsub() }
    }
}

actual class DatabaseReference internal constructor(override val jsRef: dynamic) : Query(jsRef) {
    actual val key: String? get() = jsRef.key as? String
    actual val parent: DatabaseReference? get() = jsRef.parent?.let { p -> DatabaseReference(p) }
    actual val root: DatabaseReference get() = DatabaseReference(jsRef.root)
    
    actual fun child(path: String): DatabaseReference = DatabaseReference(DatabaseModule.child(jsRef, path))
    actual fun push(): DatabaseReference = DatabaseReference(DatabaseModule.push(jsRef))
    
    actual suspend fun setValue(value: Any?) { DatabaseModule.set(jsRef, value).await<dynamic>() }
    actual suspend fun setValue(value: Any?, priority: Any?) {
        val data = js("{}")
        data[".value"] = value
        data[".priority"] = priority
        DatabaseModule.set(jsRef, data).await<dynamic>()
    }
    
    actual suspend fun updateChildren(update: Map<String, Any?>) {
        val jsUpdate = js("{}")
        update.forEach { (k, v) -> jsUpdate[k] = v }
        DatabaseModule.update(jsRef, jsUpdate).await<dynamic>()
    }
    
    actual suspend fun removeValue() { DatabaseModule.remove(jsRef).await<dynamic>() }
    
    actual suspend fun setPriority(priority: Any?) {
        val data = js("{}")
        data[".priority"] = priority
        DatabaseModule.update(jsRef, data).await<dynamic>()
    }
    
    actual fun onDisconnect(): OnDisconnect = OnDisconnect(DatabaseModule.onDisconnect(jsRef))
}

actual class DataSnapshot internal constructor(internal val jsSnap: dynamic) {
    actual val key: String? get() = jsSnap.key as? String
    actual val ref: DatabaseReference get() = DatabaseReference(jsSnap.ref)
    actual fun exists(): Boolean = jsSnap.exists() as Boolean
    actual fun hasChildren(): Boolean = jsSnap.hasChildren() as Boolean
    actual fun hasChild(path: String): Boolean = jsSnap.hasChild(path) as Boolean
    actual val childrenCount: Long get() = (jsSnap.size as Number).toLong()
    actual fun child(path: String): DataSnapshot = DataSnapshot(jsSnap.child(path))
    actual val children: Iterable<DataSnapshot> get() {
        val list = mutableListOf<DataSnapshot>()
        jsSnap.forEach { child: dynamic -> list.add(DataSnapshot(child)); false }
        return list
    }
    actual fun getValue(): Any? = jsSnap.`val`()
    actual val priority: Any? get() = jsSnap.priority
}

actual class OnDisconnect internal constructor(private val jsOD: dynamic) {
    actual suspend fun setValue(value: Any?) { jsOD.set(value).await<dynamic>() }
    actual suspend fun updateChildren(update: Map<String, Any?>) {
        val jsUpdate = js("{}")
        update.forEach { (k, v) -> jsUpdate[k] = v }
        jsOD.update(jsUpdate).await<dynamic>()
    }
    actual suspend fun removeValue() { jsOD.remove().await<dynamic>() }
    actual suspend fun cancel() { jsOD.cancel().await<dynamic>() }
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

actual class DatabaseError internal constructor(
    actual val code: Int,
    actual val message: String,
    actual val details: String? = null
) {
    actual fun toException(): Exception = DatabaseException(message)
}

actual class DatabaseException(message: String) : Exception(message)

private suspend fun <T> Promise<T>.await(): T = suspendCancellableCoroutine { cont ->
    then({ result -> cont.resume(result) }, { error -> cont.resumeWithException(Exception(error.toString())) })
}
