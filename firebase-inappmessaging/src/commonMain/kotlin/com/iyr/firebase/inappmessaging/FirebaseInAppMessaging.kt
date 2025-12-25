package com.iyr.firebase.inappmessaging

expect class FirebaseInAppMessaging {
    companion object {
        fun getInstance(): FirebaseInAppMessaging
    }
    
    fun setMessagesSuppressed(suppressed: Boolean)
    fun setAutomaticDataCollectionEnabled(enabled: Boolean)
    fun isAutomaticDataCollectionEnabled(): Boolean
    fun triggerEvent(eventName: String)
    fun addClickListener(listener: InAppMessagingClickListener)
    fun addDismissListener(listener: InAppMessagingDismissListener)
    fun addDisplayListener(listener: InAppMessagingDisplayListener)
    fun addImpressionListener(listener: InAppMessagingImpressionListener)
    fun removeClickListener(listener: InAppMessagingClickListener)
    fun removeDismissListener(listener: InAppMessagingDismissListener)
    fun removeDisplayListener(listener: InAppMessagingDisplayListener)
    fun removeImpressionListener(listener: InAppMessagingImpressionListener)
    fun clearAllListeners()
}

expect interface InAppMessagingClickListener {
    fun messageClicked(message: InAppMessage, action: InAppMessagingAction)
}

expect interface InAppMessagingDismissListener {
    fun messageDismissed(message: InAppMessage, dismissType: InAppMessagingDismissType)
}

expect interface InAppMessagingDisplayListener {
    fun messageDisplayed(message: InAppMessage)
}

expect interface InAppMessagingImpressionListener {
    fun impressionDetected(message: InAppMessage)
}

expect class InAppMessage {
    val campaignId: String?
    val campaignName: String?
    val messageType: MessageType
    val title: Text?
    val body: Text?
    val imageUrl: String?
    val actionButton: Button?
}

expect class Text {
    val text: String?
    val hexColor: String?
}

expect class Button {
    val text: Text?
    val buttonHexColor: String?
}

expect class InAppMessagingAction {
    val actionUrl: String?
    val button: Button?
}

expect enum class InAppMessagingDismissType {
    UNKNOWN_DISMISS_TYPE,
    AUTO,
    CLICK,
    SWIPE
}

expect enum class MessageType {
    CARD,
    BANNER,
    MODAL,
    IMAGE_ONLY,
    UNKNOWN
}

