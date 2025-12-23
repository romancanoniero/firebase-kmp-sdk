package com.iyr.firebase.database

import com.iyr.firebase.core.FirebaseApp
import kotlinx.coroutines.flow.Flow

expect class FirebaseDatabase {
    companion object {
        fun getInstance(): FirebaseDatabase
        fun getInstance(app: FirebaseApp): FirebaseDatabase
        fun getInstance(url: String): FirebaseDatabase
    }
    
    fun getReference(): DatabaseReference
    fun getReference(path: String): DatabaseReference
    fun getReferenceFromUrl(url: String): DatabaseReference
    fun goOnline()
    fun goOffline()
    fun setPersistenceEnabled(enabled: Boolean)
    fun setLogLevel(level: Int)
}

expect open class Query {
    fun orderByChild(path: String): Query
    fun orderByKey(): Query
    fun orderByValue(): Query
    fun orderByPriority(): Query
    fun startAt(value: String?): Query
    fun startAt(value: Double): Query
    fun startAt(value: Boolean): Query
    fun startAt(value: String?, key: String?): Query
    fun endAt(value: String?): Query
    fun endAt(value: Double): Query
    fun endAt(value: Boolean): Query
    fun endAt(value: String?, key: String?): Query
    fun equalTo(value: String?): Query
    fun equalTo(value: Double): Query
    fun equalTo(value: Boolean): Query
    fun equalTo(value: String?, key: String?): Query
    fun limitToFirst(limit: Int): Query
    fun limitToLast(limit: Int): Query
    suspend fun get(): DataSnapshot
    fun addValueEventListener(listener: ValueEventListener): ValueEventListener
    fun addChildEventListener(listener: ChildEventListener): ChildEventListener
    fun removeEventListener(listener: ValueEventListener)
    fun removeEventListener(listener: ChildEventListener)
    val valueEvents: Flow<DataSnapshot>
}

expect class DatabaseReference : Query {
    val key: String?
    val parent: DatabaseReference?
    val root: DatabaseReference
    fun child(path: String): DatabaseReference
    fun push(): DatabaseReference
    suspend fun setValue(value: Any?)
    suspend fun setValue(value: Any?, priority: Any?)
    suspend fun updateChildren(update: Map<String, Any?>)
    suspend fun removeValue()
    suspend fun setPriority(priority: Any?)
    fun onDisconnect(): OnDisconnect
}

expect class DataSnapshot {
    val key: String?
    val ref: DatabaseReference
    fun exists(): Boolean
    fun hasChildren(): Boolean
    fun hasChild(path: String): Boolean
    val childrenCount: Long
    fun child(path: String): DataSnapshot
    val children: Iterable<DataSnapshot>
    fun getValue(): Any?
    val priority: Any?
}

expect class OnDisconnect {
    suspend fun setValue(value: Any?)
    suspend fun updateChildren(update: Map<String, Any?>)
    suspend fun removeValue()
    suspend fun cancel()
}

expect interface ValueEventListener {
    fun onDataChange(snapshot: DataSnapshot)
    fun onCancelled(error: DatabaseError)
}

expect interface ChildEventListener {
    fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?)
    fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?)
    fun onChildRemoved(snapshot: DataSnapshot)
    fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?)
    fun onCancelled(error: DatabaseError)
}

expect class DatabaseError {
    val code: Int
    val message: String
    val details: String?
    fun toException(): Exception
}

expect class DatabaseException : Exception
