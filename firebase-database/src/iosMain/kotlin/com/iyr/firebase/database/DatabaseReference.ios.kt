package com.iyr.firebase.database

import cocoapods.FirebaseDatabase.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class DatabaseReference internal constructor(
    override val ios: FIRDatabaseReference
) : Query(ios) {
    actual val key: String? get() = ios.key
    actual val parent: DatabaseReference? get() = ios.parent?.let { DatabaseReference(it) }
    actual val root: DatabaseReference get() = DatabaseReference(ios.root)
    actual fun child(path: String): DatabaseReference = DatabaseReference(ios.child(path))
    actual fun push(): DatabaseReference = DatabaseReference(ios.childByAutoId())
    
    actual suspend fun setValue(value: Any?): Unit = suspendCancellableCoroutine { cont ->
        ios.setValue(value) { error, _ ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual suspend fun setValue(value: Any?, priority: Any?): Unit = suspendCancellableCoroutine { cont ->
        ios.setValue(value, andPriority = priority) { error, _ ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual suspend fun updateChildren(update: Map<String, Any?>): Unit = suspendCancellableCoroutine { cont ->
        ios.updateChildValues(update) { error, _ ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual suspend fun removeValue(): Unit = suspendCancellableCoroutine { cont ->
        ios.removeValueWithCompletionBlock { error, _ ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual suspend fun setPriority(priority: Any?): Unit = suspendCancellableCoroutine { cont ->
        ios.setPriority(priority) { error, _ ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual fun onDisconnect(): OnDisconnect = OnDisconnect(ios.onDisconnect)
}

actual class DataSnapshot internal constructor(val ios: FIRDataSnapshot) {
    actual val key: String? get() = ios.key
    actual val ref: DatabaseReference get() = DatabaseReference(ios.ref)
    actual fun exists(): Boolean = ios.exists()
    actual fun hasChildren(): Boolean = ios.hasChildren()
    actual fun hasChild(path: String): Boolean = ios.hasChild(path)
    actual val childrenCount: Long get() = ios.childrenCount.toLong()
    actual fun child(path: String): DataSnapshot = DataSnapshot(ios.childSnapshotForPath(path))
    actual val children: Iterable<DataSnapshot> get() = (ios.children.allObjects as List<FIRDataSnapshot>).map { DataSnapshot(it) }
    actual fun getValue(): Any? = ios.value
    actual val priority: Any? get() = ios.priority
}

actual class OnDisconnect internal constructor(private val ios: FIRDatabaseReference) {
    actual suspend fun setValue(value: Any?) { ios.onDisconnectSetValue(value) }
    actual suspend fun updateChildren(update: Map<String, Any?>) { ios.onDisconnectUpdateChildValues(update) }
    actual suspend fun removeValue() { ios.onDisconnectRemoveValue() }
    actual suspend fun cancel() { ios.cancelDisconnectOperations() }
}

actual class DatabaseError internal constructor(val message_: String) {
    actual val code: Int get() = 0
    actual val message: String get() = message_
    actual val details: String? get() = null
    actual fun toException(): Exception = Exception(message_)
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
