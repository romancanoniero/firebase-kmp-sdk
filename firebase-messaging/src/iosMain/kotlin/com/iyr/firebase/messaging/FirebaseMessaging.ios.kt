package com.iyr.firebase.messaging

import cocoapods.FirebaseMessaging.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class FirebaseMessaging internal constructor(val ios: FIRMessaging) {
    actual companion object {
        actual fun getInstance(): FirebaseMessaging = FirebaseMessaging(FIRMessaging.messaging())
    }
    actual suspend fun getToken(): String = suspendCancellableCoroutine { cont ->
        ios.tokenWithCompletion { token, error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(token ?: "")
        }
    }
    actual suspend fun deleteToken(): Unit = suspendCancellableCoroutine { cont ->
        ios.deleteTokenWithCompletion { error ->
            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
            else cont.resume(Unit)
        }
    }
    actual fun subscribeToTopic(topic: String) { ios.subscribeToTopic(topic) }
    actual fun unsubscribeFromTopic(topic: String) { ios.unsubscribeFromTopic(topic) }
    actual var isAutoInitEnabled: Boolean
        get() = ios.isAutoInitEnabled()
        set(value) { ios.setAutoInitEnabled(value) }
    actual var deliveryMetricsExportToBigQueryEnabled: Boolean = false
    actual var isNotificationDelegationEnabled: Boolean = false
}

// En iOS, los mensajes push se reciben via APNs/UNUserNotificationCenter, no via clases de Firebase
// RemoteMessage es un wrapper para mantener compatibilidad de API
actual class RemoteMessage internal constructor(private val messageData: Map<String, String>?) {
    actual val collapseKey: String? get() = messageData?.get("collapse_key")
    actual val data: Map<String, String> get() = messageData ?: emptyMap()
    actual val from: String? get() = messageData?.get("from")
    actual val messageId: String? get() = messageData?.get("message_id")
    actual val messageType: String? get() = messageData?.get("message_type")
    actual val notification: Notification? get() = null
    actual val originalPriority: Int get() = 0
    actual val priority: Int get() = 0
    actual val sentTime: Long get() = 0
    actual val to: String? get() = messageData?.get("to")
    actual val ttl: Int get() = 0
    
    actual class Notification {
        actual val body: String? get() = null
        actual val bodyLocalizationArgs: Array<String>? get() = null
        actual val bodyLocalizationKey: String? get() = null
        actual val channelId: String? get() = null
        actual val clickAction: String? get() = null
        actual val color: String? get() = null
        actual val defaultLightSettings: Boolean get() = false
        actual val defaultSound: Boolean get() = false
        actual val defaultVibrateSettings: Boolean get() = false
        actual val icon: String? get() = null
        actual val imageUrl: String? get() = null
        actual val lightSettings: IntArray? get() = null
        actual val link: String? get() = null
        actual val localOnly: Boolean get() = false
        actual val notificationCount: Int? get() = null
        actual val notificationPriority: Int? get() = null
        actual val sound: String? get() = null
        actual val sticky: Boolean get() = false
        actual val tag: String? get() = null
        actual val ticker: String? get() = null
        actual val title: String? get() = null
        actual val titleLocalizationArgs: Array<String>? get() = null
        actual val titleLocalizationKey: String? get() = null
        actual val vibrateTimings: LongArray? get() = null
        actual val visibility: Int? get() = null
    }
    
    actual class Builder actual constructor(to: String) {
        private var data = mutableMapOf<String, String>()
        actual fun setCollapseKey(collapseKey: String?): Builder = this
        actual fun setData(data: Map<String, String>): Builder { this.data = data.toMutableMap(); return this }
        actual fun setMessageId(messageId: String): Builder = this
        actual fun setMessageType(messageType: String?): Builder = this
        actual fun setTtl(ttl: Int): Builder = this
        actual fun addData(key: String, value: String?): Builder { value?.let { data[key] = it }; return this }
        actual fun clearData(): Builder { data.clear(); return this }
        actual fun build(): RemoteMessage = RemoteMessage(data.toMap())
    }
}

// iOS handles messaging via AppDelegate, not a service class
actual abstract class FirebaseMessagingService {
    actual open fun onMessageReceived(remoteMessage: RemoteMessage) {}
    actual open fun onDeletedMessages() {}
    actual open fun onMessageSent(msgId: String) {}
    actual open fun onSendError(msgId: String, exception: Exception) {}
    actual open fun onNewToken(token: String) {}
}

actual class MessagingException(override val message: String?, actual val code: Int) : Exception(message)
