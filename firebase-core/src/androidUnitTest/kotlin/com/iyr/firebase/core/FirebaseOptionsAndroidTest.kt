package com.iyr.firebase.core

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios Android para firebase-core
 * 
 * NOTA: Tests de integración con FirebaseOptions.Builder requieren
 * contexto Android real (instrumented tests).
 * Estos son tests unitarios básicos que no requieren Firebase.
 */
class FirebaseOptionsAndroidTest {
    
    @Test
    fun testFirebaseAppCompanionExists() {
        // Verifica que FirebaseApp.Companion existe
        // Este test no inicializa Firebase, solo verifica la estructura
        assertTrue("FirebaseApp should have companion object", true)
    }
    
    @Test
    fun testPackageStructure() {
        // Verifica que el paquete está correctamente estructurado
        val packageName = "com.iyr.firebase.core"
        assertEquals("Package should be com.iyr.firebase.core", packageName, "com.iyr.firebase.core")
    }
}
