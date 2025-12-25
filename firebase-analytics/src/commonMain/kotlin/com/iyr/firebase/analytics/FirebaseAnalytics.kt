package com.iyr.firebase.analytics

import com.iyr.firebase.core.FirebaseApp

/**
 * FirebaseAnalytics - API multiplataforma para Firebase Analytics
 * 
 * Replica la API del Firebase Android SDK para Kotlin Multiplatform.
 */
expect class FirebaseAnalytics {
    companion object {
        fun getInstance(): FirebaseAnalytics
        fun getInstance(app: FirebaseApp): FirebaseAnalytics
    }
    
    /**
     * Registra un evento de analytics
     */
    fun logEvent(name: String, params: Map<String, Any?>?)
    
    /**
     * Establece el ID de usuario
     */
    fun setUserId(userId: String?)
    
    /**
     * Establece una propiedad de usuario
     */
    fun setUserProperty(name: String, value: String?)
    
    /**
     * Establece el ID de sesión actual
     */
    fun setSessionTimeoutDuration(milliseconds: Long)
    
    /**
     * Habilita/deshabilita la recolección de analytics
     */
    fun setAnalyticsCollectionEnabled(enabled: Boolean)
    
    /**
     * Resetea los datos de analytics
     */
    fun resetAnalyticsData()
    
    /**
     * Obtiene el App Instance ID
     */
    suspend fun getAppInstanceId(): String?
    
    /**
     * Establece pantalla actual (para automatic screen tracking)
     */
    fun setCurrentScreen(screenName: String, screenClass: String?)
    
    /**
     * Establece el consentimiento del usuario
     */
    fun setConsent(consentSettings: Map<ConsentType, ConsentStatus>)
    
    /**
     * Establece valores por defecto para eventos
     */
    fun setDefaultEventParameters(params: Map<String, Any?>?)
}

/**
 * Tipos de consentimiento
 */
enum class ConsentType {
    AD_STORAGE,
    AD_USER_DATA,
    AD_PERSONALIZATION,
    ANALYTICS_STORAGE
}

/**
 * Estados de consentimiento
 */
enum class ConsentStatus {
    GRANTED,
    DENIED
}

/**
 * Nombres de eventos predefinidos
 */
object AnalyticsEvent {
    const val ADD_PAYMENT_INFO = "add_payment_info"
    const val ADD_SHIPPING_INFO = "add_shipping_info"
    const val ADD_TO_CART = "add_to_cart"
    const val ADD_TO_WISHLIST = "add_to_wishlist"
    const val APP_OPEN = "app_open"
    const val BEGIN_CHECKOUT = "begin_checkout"
    const val EARN_VIRTUAL_CURRENCY = "earn_virtual_currency"
    const val GENERATE_LEAD = "generate_lead"
    const val JOIN_GROUP = "join_group"
    const val LEVEL_END = "level_end"
    const val LEVEL_START = "level_start"
    const val LEVEL_UP = "level_up"
    const val LOGIN = "login"
    const val POST_SCORE = "post_score"
    const val PURCHASE = "purchase"
    const val REFUND = "refund"
    const val REMOVE_FROM_CART = "remove_from_cart"
    const val SCREEN_VIEW = "screen_view"
    const val SEARCH = "search"
    const val SELECT_CONTENT = "select_content"
    const val SELECT_ITEM = "select_item"
    const val SELECT_PROMOTION = "select_promotion"
    const val SHARE = "share"
    const val SIGN_UP = "sign_up"
    const val SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency"
    const val TUTORIAL_BEGIN = "tutorial_begin"
    const val TUTORIAL_COMPLETE = "tutorial_complete"
    const val UNLOCK_ACHIEVEMENT = "unlock_achievement"
    const val VIEW_CART = "view_cart"
    const val VIEW_ITEM = "view_item"
    const val VIEW_ITEM_LIST = "view_item_list"
    const val VIEW_PROMOTION = "view_promotion"
    const val VIEW_SEARCH_RESULTS = "view_search_results"
}

/**
 * Nombres de parámetros predefinidos
 */
object AnalyticsParam {
    const val ACHIEVEMENT_ID = "achievement_id"
    const val AD_FORMAT = "ad_format"
    const val AD_PLATFORM = "ad_platform"
    const val AD_SOURCE = "ad_source"
    const val AD_UNIT_NAME = "ad_unit_name"
    const val AFFILIATION = "affiliation"
    const val CONTENT_TYPE = "content_type"
    const val COUPON = "coupon"
    const val CURRENCY = "currency"
    const val ITEM_BRAND = "item_brand"
    const val ITEM_CATEGORY = "item_category"
    const val ITEM_ID = "item_id"
    const val ITEM_LIST_ID = "item_list_id"
    const val ITEM_LIST_NAME = "item_list_name"
    const val ITEM_NAME = "item_name"
    const val ITEMS = "items"
    const val LEVEL = "level"
    const val LEVEL_NAME = "level_name"
    const val LOCATION_ID = "location_id"
    const val METHOD = "method"
    const val PAYMENT_TYPE = "payment_type"
    const val PRICE = "price"
    const val QUANTITY = "quantity"
    const val SCORE = "score"
    const val SCREEN_CLASS = "screen_class"
    const val SCREEN_NAME = "screen_name"
    const val SEARCH_TERM = "search_term"
    const val SHIPPING = "shipping"
    const val SHIPPING_TIER = "shipping_tier"
    const val TAX = "tax"
    const val TRANSACTION_ID = "transaction_id"
    const val VALUE = "value"
    const val VIRTUAL_CURRENCY_NAME = "virtual_currency_name"
}

