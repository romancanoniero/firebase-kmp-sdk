package com.iyr.firebase.inappmessaging

import com.google.firebase.inappmessaging.FirebaseInAppMessaging as AndroidIAM
import com.google.firebase.inappmessaging.model.InAppMessage as AndroidMessage
import com.google.firebase.inappmessaging.model.Action as AndroidAction
import com.google.firebase.inappmessaging.model.Text as AndroidText
import com.google.firebase.inappmessaging.model.Button as AndroidButton
import com.google.firebase.inappmessaging.model.MessageType as AndroidMessageType
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener as AndroidClickListener
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDismissListener as AndroidDismissListener
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayCallbacks.InAppMessagingDismissType as AndroidDismissType
import com.google.firebase.inappmessaging.FirebaseInAppMessagingDisplayErrorListener as AndroidDisplayListener
import com.google.firebase.inappmessaging.FirebaseInAppMessagingImpressionListener as AndroidImpressionListener

actual class FirebaseInAppMessaging internal constructor(
    private val android: AndroidIAM
) {
    actual companion object {
        actual fun getInstance(): FirebaseInAppMessaging = 
            FirebaseInAppMessaging(AndroidIAM.getInstance())
    }
    
    actual fun setMessagesSuppressed(suppressed: Boolean) {
        android.setMessagesSuppressed(suppressed)
    }
    
    actual fun setAutomaticDataCollectionEnabled(enabled: Boolean) {
        android.isAutomaticDataCollectionEnabled = enabled
    }
    
    actual fun isAutomaticDataCollectionEnabled(): Boolean = 
        android.isAutomaticDataCollectionEnabled
    
    actual fun triggerEvent(eventName: String) {
        android.triggerEvent(eventName)
    }
    
    private val clickListeners = mutableMapOf<InAppMessagingClickListener, AndroidClickListener>()
    
    actual fun addClickListener(listener: InAppMessagingClickListener) {
        val androidListener = AndroidClickListener { message, action ->
            listener.messageClicked(InAppMessage(message), InAppMessagingAction(action))
        }
        clickListeners[listener] = androidListener
        android.addClickListener(androidListener)
    }
    
    actual fun removeClickListener(listener: InAppMessagingClickListener) {
        clickListeners.remove(listener)?.let { android.removeClickListener(it) }
    }
    
    private val dismissListeners = mutableMapOf<InAppMessagingDismissListener, AndroidDismissListener>()
    
    actual fun addDismissListener(listener: InAppMessagingDismissListener) {
        val androidListener = AndroidDismissListener { message ->
            listener.messageDismissed(InAppMessage(message), InAppMessagingDismissType.UNKNOWN_DISMISS_TYPE)
        }
        dismissListeners[listener] = androidListener
        android.addDismissListener(androidListener)
    }
    
    actual fun removeDismissListener(listener: InAppMessagingDismissListener) {
        dismissListeners.remove(listener)?.let { android.removeDismissListener(it) }
    }
    
    actual fun addDisplayListener(listener: InAppMessagingDisplayListener) {
        // Android uses display error listener, adapt as needed
    }
    
    actual fun removeDisplayListener(listener: InAppMessagingDisplayListener) {}
    
    private val impressionListeners = mutableMapOf<InAppMessagingImpressionListener, AndroidImpressionListener>()
    
    actual fun addImpressionListener(listener: InAppMessagingImpressionListener) {
        val androidListener = AndroidImpressionListener { message ->
            listener.impressionDetected(InAppMessage(message))
        }
        impressionListeners[listener] = androidListener
        android.addImpressionListener(androidListener)
    }
    
    actual fun removeImpressionListener(listener: InAppMessagingImpressionListener) {
        impressionListeners.remove(listener)?.let { android.removeImpressionListener(it) }
    }
    
    actual fun clearAllListeners() {
        clickListeners.keys.toList().forEach { removeClickListener(it) }
        dismissListeners.keys.toList().forEach { removeDismissListener(it) }
        impressionListeners.keys.toList().forEach { removeImpressionListener(it) }
        clickListeners.clear()
        dismissListeners.clear()
        impressionListeners.clear()
    }
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

actual class InAppMessage internal constructor(private val android: AndroidMessage) {
    actual val campaignId: String? get() = android.campaignMetadata?.campaignId
    actual val campaignName: String? get() = android.campaignMetadata?.campaignName
    actual val messageType: MessageType get() = when (android.messageType) {
        AndroidMessageType.CARD -> MessageType.CARD
        AndroidMessageType.BANNER -> MessageType.BANNER
        AndroidMessageType.MODAL -> MessageType.MODAL
        AndroidMessageType.IMAGE_ONLY -> MessageType.IMAGE_ONLY
        else -> MessageType.UNKNOWN
    }
    actual val title: Text? get() = android.title?.let { Text(it) }
    actual val body: Text? get() = android.body?.let { Text(it) }
    actual val imageUrl: String? get() = android.imageUrl
    actual val actionButton: Button? get() = android.action?.button?.let { Button(it) }
}

actual class Text internal constructor(private val android: AndroidText) {
    actual val text: String? get() = android.text
    actual val hexColor: String? get() = android.hexColor
}

actual class Button internal constructor(private val android: AndroidButton) {
    actual val text: Text? get() = android.text?.let { Text(it) }
    actual val buttonHexColor: String? get() = android.buttonHexColor
}

actual class InAppMessagingAction internal constructor(private val android: AndroidAction) {
    actual val actionUrl: String? get() = android.actionUrl
    actual val button: Button? get() = android.button?.let { Button(it) }
}

actual enum class InAppMessagingDismissType {
    UNKNOWN_DISMISS_TYPE, AUTO, CLICK, SWIPE
}

actual enum class MessageType {
    CARD, BANNER, MODAL, IMAGE_ONLY, UNKNOWN
}

