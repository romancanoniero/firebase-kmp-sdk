package com.iyr.firebase.database

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
 * Tests de integración para FirebaseDatabase con Firebase Emulator
 * 
 * Para ejecutar estos tests, el Firebase Emulator debe estar corriendo:
 * firebase emulators:start --only database
 */
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {
    
    companion object {
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
                    .setDatabaseUrl("http://10.0.2.2:9000?ns=test-project")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        }
    }
    
    private lateinit var database: com.iyr.firebase.database.FirebaseDatabase
    
    @Before
    fun setup() {
        database = com.iyr.firebase.database.FirebaseDatabase.getInstance()
    }
    
    @Test
    fun testSetValue() = runBlocking {
        val ref = database.getReference("test/integration/setValue_${System.currentTimeMillis()}")
        ref.setValue("Hello World")
        
        val snapshot = ref.get()
        assertEquals("Hello World", snapshot.getValue())
    }
    
    @Test
    fun testSetMap() = runBlocking {
        val ref = database.getReference("test/integration/setMap_${System.currentTimeMillis()}")
        val data = mapOf(
            "name" to "Test",
            "value" to 123,
            "active" to true
        )
        ref.setValue(data)
        
        val snapshot = ref.get()
        assertNotNull(snapshot.getValue())
        assertTrue(snapshot.exists())
    }
    
    @Test
    fun testPush() = runBlocking {
        val ref = database.getReference("test/integration/pushTest")
        val newRef = ref.push()
        
        assertNotNull(newRef.key)
        assertTrue(newRef.key!!.isNotEmpty())
    }
}
