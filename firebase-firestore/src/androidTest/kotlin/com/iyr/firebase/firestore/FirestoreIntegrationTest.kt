package com.iyr.firebase.firestore

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Tests de integración para FirebaseFirestore con Firebase Emulator
 * 
 * Para ejecutar estos tests, el Firebase Emulator debe estar corriendo:
 * firebase emulators:start --only firestore
 */
@RunWith(AndroidJUnit4::class)
class FirestoreIntegrationTest {
    
    companion object {
        private var firestore: com.iyr.firebase.firestore.FirebaseFirestore? = null
        
        @JvmStatic
        @BeforeClass
        fun initializeFirebase() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            
            try {
                FirebaseApp.getInstance()
            } catch (e: Exception) {
                val options = FirebaseOptions.Builder()
                    .setApiKey("test-api-key")
                    .setApplicationId("1:123456789:android:abcdef")
                    .setProjectId("test-project")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            
            // Initialize Firestore and connect to emulator ONCE
            firestore = com.iyr.firebase.firestore.FirebaseFirestore.getInstance()
            try {
                firestore!!.useEmulator("10.0.2.2", 8080)
            } catch (e: Exception) {
                // Already connected to emulator
            }
        }
    }
    
    private lateinit var fs: com.iyr.firebase.firestore.FirebaseFirestore
    
    @Before
    fun setup() {
        fs = firestore!!
    }
    
    @Test
    fun testAddDocument() = runBlocking {
        val collection = fs.collection("test_integration")
        val data = mapOf(
            "name" to "Test Document",
            "timestamp" to System.currentTimeMillis()
        )
        
        val docRef = collection.add(data)
        assertNotNull(docRef)
        assertNotNull(docRef.id)
        assertTrue(docRef.id.isNotEmpty())
    }
    
    @Test
    fun testSetDocument() = runBlocking {
        val docRef = fs.document("test_integration/set_test_${System.currentTimeMillis()}")
        val data = mapOf(
            "name" to "Set Test",
            "value" to 42
        )
        
        docRef.set(data, SetOptions.overwrite())
        
        val snapshot = docRef.get()
        assertTrue(snapshot.exists())
        assertEquals("Set Test", snapshot.get("name"))
    }
    
    @Test
    fun testDeleteDocument() = runBlocking {
        val docRef = fs.document("test_integration/delete_test_${System.currentTimeMillis()}")
        
        // Create document
        docRef.set(mapOf("temp" to true), SetOptions.overwrite())
        assertTrue(docRef.get().exists())
        
        // Delete
        docRef.delete()
        assertFalse(docRef.get().exists())
    }
}
