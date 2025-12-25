package com.iyr.firebase.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios para Analytics Constants
 */
class AnalyticsConstantsTest {
    
    @Test
    fun testAnalyticsEventConstants() {
        assertEquals("add_payment_info", AnalyticsEvent.ADD_PAYMENT_INFO)
        assertEquals("add_to_cart", AnalyticsEvent.ADD_TO_CART)
        assertEquals("login", AnalyticsEvent.LOGIN)
        assertEquals("sign_up", AnalyticsEvent.SIGN_UP)
        assertEquals("purchase", AnalyticsEvent.PURCHASE)
        assertEquals("screen_view", AnalyticsEvent.SCREEN_VIEW)
        assertEquals("search", AnalyticsEvent.SEARCH)
        assertEquals("share", AnalyticsEvent.SHARE)
    }
    
    @Test
    fun testAnalyticsParamConstants() {
        assertEquals("item_id", AnalyticsParam.ITEM_ID)
        assertEquals("item_name", AnalyticsParam.ITEM_NAME)
        assertEquals("currency", AnalyticsParam.CURRENCY)
        assertEquals("value", AnalyticsParam.VALUE)
        assertEquals("screen_name", AnalyticsParam.SCREEN_NAME)
        assertEquals("screen_class", AnalyticsParam.SCREEN_CLASS)
        assertEquals("method", AnalyticsParam.METHOD)
    }
    
    @Test
    fun testConsentTypeEnum() {
        val types = ConsentType.values()
        assertEquals(4, types.size)
        assertTrue(types.contains(ConsentType.AD_STORAGE))
        assertTrue(types.contains(ConsentType.AD_USER_DATA))
        assertTrue(types.contains(ConsentType.AD_PERSONALIZATION))
        assertTrue(types.contains(ConsentType.ANALYTICS_STORAGE))
    }
    
    @Test
    fun testConsentStatusEnum() {
        val statuses = ConsentStatus.values()
        assertEquals(2, statuses.size)
        assertTrue(statuses.contains(ConsentStatus.GRANTED))
        assertTrue(statuses.contains(ConsentStatus.DENIED))
    }
}
