package com.iyr.firebase.firestore

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests unitarios para Firestore SetOptions y enums
 */
class SetOptionsTest {
    
    @Test
    fun testDirectionEnum() {
        val values = Direction.values()
        assertTrue(values.size == 2)
        assertTrue(values.contains(Direction.ASCENDING))
        assertTrue(values.contains(Direction.DESCENDING))
    }
    
    @Test
    fun testDocumentChangeTypeEnum() {
        val values = DocumentChangeType.values()
        assertTrue(values.size == 3)
        assertTrue(values.contains(DocumentChangeType.ADDED))
        assertTrue(values.contains(DocumentChangeType.MODIFIED))
        assertTrue(values.contains(DocumentChangeType.REMOVED))
    }
}
