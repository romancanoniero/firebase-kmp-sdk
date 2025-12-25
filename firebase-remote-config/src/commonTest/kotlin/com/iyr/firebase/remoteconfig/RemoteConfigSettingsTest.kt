package com.iyr.firebase.remoteconfig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios para Remote Config - Enums y constantes
 */
class RemoteConfigSettingsTest {
    
    @Test
    fun testValueSourceEnum() {
        val sources = ValueSource.values()
        assertEquals(3, sources.size)
        
        assertTrue(sources.contains(ValueSource.STATIC))
        assertTrue(sources.contains(ValueSource.DEFAULT))
        assertTrue(sources.contains(ValueSource.REMOTE))
    }
    
    @Test
    fun testExceptionCodes() {
        val codes = FirebaseRemoteConfigException.Code.values()
        assertEquals(4, codes.size)
        
        assertTrue(codes.contains(FirebaseRemoteConfigException.Code.UNKNOWN))
        assertTrue(codes.contains(FirebaseRemoteConfigException.Code.THROTTLED))
        assertTrue(codes.contains(FirebaseRemoteConfigException.Code.INTERNAL))
        assertTrue(codes.contains(FirebaseRemoteConfigException.Code.FETCH_ERROR))
    }
}
