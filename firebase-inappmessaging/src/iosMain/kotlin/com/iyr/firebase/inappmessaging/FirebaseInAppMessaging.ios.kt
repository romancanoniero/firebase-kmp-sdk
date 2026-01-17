package com.iyr.firebase.inappmessaging

import cocoapods.FirebaseInAppMessaging.*
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseInAppMessaging internal constructor(
    private val ios: FIRInAppMessaging
) {
    actual companion object {
        actual fun getInstance(): FirebaseInAppMessaging = 
            FirebaseInAppMessaging(FIRInAppMessaging.inAppMessaging())
    }
    
    actual fun setMessagesSuppressed(suppressed: Boolean) {
        ios.setMessageDisplaySuppressed(suppressed)
    }
    
    actual fun setAutomaticDataCollectionEnabled(enabled: Boolean) {
        ios.setAutomaticDataCollectionEnabled(enabled)
    }
    
    actual fun isAutomaticDataCollectionEnabled(): Boolean = 
        ios.automaticDataCollectionEnabled
    
    actual fun triggerEvent(eventName: String) {
        ios.triggerEvent(eventName)
    }
    
    actual fun addClickListener(listener: InAppMessagingClickListener) {
        // iOS uses delegate pattern
    }
    
    actual fun removeClickListener(listener: InAppMessagingClickListener) {}
    
    actual fun addDismissListener(listener: InAppMessagingDismissListener) {}
    actual fun removeDismissListener(listener: InAppMessagingDismissListener) {}
    
    actual fun addDisplayListener(listener: InAppMessagingDisplayListener) {}
    actual fun removeDisplayListener(listener: InAppMessagingDisplayListener) {}
    
    actual fun addImpressionListener(listener: InAppMessagingImpressionListener) {}
    actual fun removeImpressionListener(listener: InAppMessagingImpressionListener) {}
    
    actual fun clearAllListeners() {}
}

actual interface InAppMessagingClickListener {
    actual fun messageClicked(message: InAppMessage, action: InAppMessagingAction)
}

actual interface InAppMessagingDismissListener {
    actual fun messageDismissed(message: InAppMessage, dismissType: InAppMessagingDismissType)
}

actual interface InAppMessagingDisplayListener {
    actual fun messageDisplayed(message: InAppMessage)
}

actual interface InAppMessagingImpressionListener {
    actual fun impressionDetected(message: InAppMessage)
}

actual class InAppMessage {
    actual val campaignId: String? get() = null
    actual val campaignName: String? get() = null
    actual val messageType: MessageType get() = MessageType.UNKNOWN
    actual val title: Text? get() = null
    actual val body: Text? get() = null
    actual val imageUrl: String? get() = null
    actual val actionButton: Button? get() = null
}

actual class Text {
    actual val text: String? get() = null
    actual val hexColor: String? get() = null
}

actual class Button {
    actual val text: Text? get() = null
    actual val buttonHexColor: String? get() = null
}

actual class InAppMessagingAction {
    actual val actionUrl: String? get() = null
    actual val button: Button? get() = null
}

actual enum class InAppMessagingDismissType {
    UNKNOWN_DISMISS_TYPE, AUTO, CLICK, SWIPE
}

actual enum class MessageType {
    CARD, BANNER, MODAL, IMAGE_ONLY, UNKNOWN
}






