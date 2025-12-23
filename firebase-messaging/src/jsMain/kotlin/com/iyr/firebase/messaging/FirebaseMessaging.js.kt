package com.iyr.firebase.messaging

actual class FirebaseMessaging internal constructor(val js: dynamic) {
    actual companion object {
        actual fun getInstance(): FirebaseMessaging = FirebaseMessaging(js("{}"))
    }
    actual suspend fun getToken(): String = ""
    actual suspend fun deleteToken() {}
    actual fun subscribeToTopic(topic: String) {}
    actual fun unsubscribeFromTopic(topic: String) {}
    actual var isAutoInitEnabled: Boolean = true
    actual var deliveryMetricsExportToBigQueryEnabled: Boolean = false
    actual var isNotificationDelegationEnabled: Boolean = false
}

actual class RemoteMessage internal constructor(val js: dynamic) {
    actual val collapseKey: String? get() = null
    actual val data: Map<String, String> get() = emptyMap()
    actual val from: String? get() = null
    actual val messageId: String? get() = null
    actual val messageType: String? get() = null
    actual val notification: Notification? get() = null
    actual val originalPriority: Int get() = 0
    actual val priority: Int get() = 0
    actual val sentTime: Long get() = 0
    actual val to: String? get() = null
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
        actual fun setCollapseKey(collapseKey: String?): Builder = this
        actual fun setData(data: Map<String, String>): Builder = this
        actual fun setMessageId(messageId: String): Builder = this
        actual fun setMessageType(messageType: String?): Builder = this
        actual fun setTtl(ttl: Int): Builder = this
        actual fun addData(key: String, value: String?): Builder = this
        actual fun clearData(): Builder = this
        actual fun build(): RemoteMessage = RemoteMessage(js("{}"))
    }
}

actual abstract class FirebaseMessagingService {
    actual open fun onMessageReceived(remoteMessage: RemoteMessage) {}
    actual open fun onDeletedMessages() {}
    actual open fun onMessageSent(msgId: String) {}
    actual open fun onSendError(msgId: String, exception: Exception) {}
    actual open fun onNewToken(token: String) {}
}

actual class MessagingException(override val message: String?, actual val code: Int) : Exception(message)
