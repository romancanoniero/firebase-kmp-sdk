package com.iyr.firebase.database

import com.google.firebase.database.DatabaseReference as AndroidRef
import com.google.firebase.database.OnDisconnect as AndroidOnDisconnect
import kotlinx.coroutines.tasks.await

actual class DatabaseReference internal constructor(
    override val android: AndroidRef
) : Query(android) {
    actual val key: String? get() = android.key
    actual val parent: DatabaseReference? get() = android.parent?.let { DatabaseReference(it) }
    actual val root: DatabaseReference get() = DatabaseReference(android.root)
    actual fun child(path: String): DatabaseReference = DatabaseReference(android.child(path))
    actual fun push(): DatabaseReference = DatabaseReference(android.push())
    actual suspend fun setValue(value: Any?) { android.setValue(value).await() }
    actual suspend fun setValue(value: Any?, priority: Any?) { android.setValue(value, priority).await() }
    actual suspend fun updateChildren(update: Map<String, Any?>) { android.updateChildren(update).await() }
    actual suspend fun removeValue() { android.removeValue().await() }
    actual suspend fun setPriority(priority: Any?) { android.setPriority(priority).await() }
    actual fun onDisconnect(): OnDisconnect = OnDisconnect(android.onDisconnect())
}

actual class DataSnapshot internal constructor(val android: com.google.firebase.database.DataSnapshot) {
    actual val key: String? get() = android.key
    actual val ref: DatabaseReference get() = DatabaseReference(android.ref)
    actual fun exists(): Boolean = android.exists()
    actual fun hasChildren(): Boolean = android.hasChildren()
    actual fun hasChild(path: String): Boolean = android.hasChild(path)
    actual val childrenCount: Long get() = android.childrenCount
    actual fun child(path: String): DataSnapshot = DataSnapshot(android.child(path))
    actual val children: Iterable<DataSnapshot> get() = android.children.map { DataSnapshot(it) }
    actual fun getValue(): Any? = android.value
    actual val priority: Any? get() = android.priority
}

actual class OnDisconnect internal constructor(private val android: AndroidOnDisconnect) {
    actual suspend fun setValue(value: Any?) { android.setValue(value).await() }
    actual suspend fun updateChildren(update: Map<String, Any?>) { android.updateChildren(update).await() }
    actual suspend fun removeValue() { android.removeValue().await() }
    actual suspend fun cancel() { android.cancel().await() }
}

actual class DatabaseError internal constructor(private val android: com.google.firebase.database.DatabaseError) {
    actual val code: Int get() = android.code
    actual val message: String get() = android.message
    actual val details: String? get() = android.details
    actual fun toException(): Exception = android.toException()
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
