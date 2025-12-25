package com.iyr.firebase.auth

import android.content.Context
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
 * Tests de integración para FirebaseAuth con Firebase Emulator
 * 
 * Para ejecutar estos tests, el Firebase Emulator debe estar corriendo:
 * firebase emulators:start --only auth
 */
@RunWith(AndroidJUnit4::class)
class AuthIntegrationTest {
    
    companion object {
        @JvmStatic
        @BeforeClass
        fun initializeFirebase() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            
            // Check if already initialized
            try {
                FirebaseApp.getInstance()
            } catch (e: Exception) {
                // Initialize with test config using Android Firebase SDK directly
                val options = FirebaseOptions.Builder()
                    .setApiKey("test-api-key")
                    .setApplicationId("1:123456789:android:abcdef")
                    .setProjectId("test-project")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        }
    }
    
    private lateinit var auth: com.iyr.firebase.auth.FirebaseAuth
    
    @Before
    fun setup() {
        // Conectar al emulator usando la clase KMP
        auth = com.iyr.firebase.auth.FirebaseAuth.getInstance()
        auth.useEmulator("10.0.2.2", 9099) // 10.0.2.2 es localhost desde el emulator Android
    }
    
    @Test
    fun testSignInAnonymously() = runBlocking {
        val result = auth.signInAnonymously()
        assertNotNull(result)
        assertNotNull(result.user)
        assertTrue(result.user!!.isAnonymous)
    }
    
    @Test
    fun testCreateUserWithEmailAndPassword() = runBlocking {
        val email = "test_${System.currentTimeMillis()}@test.com"
        val password = "password123"
        
        val result = auth.createUserWithEmailAndPassword(email, password)
        assertNotNull(result)
        assertNotNull(result.user)
        assertEquals(email, result.user!!.email)
    }
    
    @Test
    fun testSignOut() = runBlocking {
        // Sign in anonimamente
        auth.signInAnonymously()
        assertNotNull(auth.currentUser)
        
        // Sign out
        auth.signOut()
        assertNull(auth.currentUser)
    }
}
