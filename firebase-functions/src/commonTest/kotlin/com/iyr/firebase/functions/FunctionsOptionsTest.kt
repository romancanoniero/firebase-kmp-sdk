package com.iyr.firebase.functions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios para Functions - Enums y constantes
 */
class FunctionsOptionsTest {
    
    @Test
    fun testTimeUnitValues() {
        val units = TimeUnit.values()
        assertEquals(7, units.size)
        
        assertTrue(units.contains(TimeUnit.NANOSECONDS))
        assertTrue(units.contains(TimeUnit.MICROSECONDS))
        assertTrue(units.contains(TimeUnit.MILLISECONDS))
        assertTrue(units.contains(TimeUnit.SECONDS))
        assertTrue(units.contains(TimeUnit.MINUTES))
        assertTrue(units.contains(TimeUnit.HOURS))
        assertTrue(units.contains(TimeUnit.DAYS))
    }
    
    @Test
    fun testFunctionsExceptionCodes() {
        val codes = FirebaseFunctionsException.Code.values()
        assertEquals(17, codes.size)
        
        assertTrue(codes.contains(FirebaseFunctionsException.Code.OK))
        assertTrue(codes.contains(FirebaseFunctionsException.Code.CANCELLED))
        assertTrue(codes.contains(FirebaseFunctionsException.Code.UNKNOWN))
        assertTrue(codes.contains(FirebaseFunctionsException.Code.INVALID_ARGUMENT))
        assertTrue(codes.contains(FirebaseFunctionsException.Code.PERMISSION_DENIED))
        assertTrue(codes.contains(FirebaseFunctionsException.Code.UNAUTHENTICATED))
    }
}
