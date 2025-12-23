package com.iyr.firebase.messaging

import com.google.firebase.messaging.FirebaseMessaging as AndroidMessaging
import com.google.firebase.messaging.RemoteMessage as AndroidRemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService as AndroidService
import kotlinx.coroutines.tasks.await

actual class FirebaseMessaging internal constructor(private val android: AndroidMessaging) {
    actual companion object {
        actual fun getInstance(): FirebaseMessaging = FirebaseMessaging(AndroidMessaging.getInstance())
    }
    actual suspend fun getToken(): String = android.token.await()
    actual suspend fun deleteToken() { android.deleteToken().await() }
    actual fun subscribeToTopic(topic: String) { android.subscribeToTopic(topic) }
    actual fun unsubscribeFromTopic(topic: String) { android.unsubscribeFromTopic(topic) }
    actual var isAutoInitEnabled: Boolean
        get() = android.isAutoInitEnabled
        set(value) { android.isAutoInitEnabled = value }
    actual var deliveryMetricsExportToBigQueryEnabled: Boolean = false
    actual var isNotificationDelegationEnabled: Boolean
        get() = android.isNotificationDelegationEnabled
        set(value) { android.setNotificationDelegationEnabled(value) }
}

actual class RemoteMessage internal constructor(val android: AndroidRemoteMessage) {
    actual val collapseKey: String? get() = android.collapseKey
    actual val data: Map<String, String> get() = android.data
    actual val from: String? get() = android.from
    actual val messageId: String? get() = android.messageId
    actual val messageType: String? get() = android.messageType
    actual val notification: Notification? get() = android.notification?.let { Notification(it) }
    actual val originalPriority: Int get() = android.originalPriority
    actual val priority: Int get() = android.priority
    actual val sentTime: Long get() = android.sentTime
    actual val to: String? get() = android.to
    actual val ttl: Int get() = android.ttl
    
    actual class Notification internal constructor(private val android: AndroidRemoteMessage.Notification) {
        actual val body: String? get() = android.body
        actual val bodyLocalizationArgs: Array<String>? get() = android.bodyLocalizationArgs
        actual val bodyLocalizationKey: String? get() = android.bodyLocalizationKey
        actual val channelId: String? get() = android.channelId
        actual val clickAction: String? get() = android.clickAction
        actual val color: String? get() = android.color
        actual val defaultLightSettings: Boolean get() = android.defaultLightSettings
        actual val defaultSound: Boolean get() = android.defaultSound
        actual val defaultVibrateSettings: Boolean get() = android.defaultVibrateSettings
        actual val icon: String? get() = android.icon
        actual val imageUrl: String? get() = android.imageUrl?.toString()
        actual val lightSettings: IntArray? get() = android.lightSettings
        actual val link: String? get() = android.link?.toString()
        actual val localOnly: Boolean get() = android.localOnly
        actual val notificationCount: Int? get() = android.notificationCount
        actual val notificationPriority: Int? get() = android.notificationPriority
        actual val sound: String? get() = android.sound
        actual val sticky: Boolean get() = android.sticky
        actual val tag: String? get() = android.tag
        actual val ticker: String? get() = android.ticker
        actual val title: String? get() = android.title
        actual val titleLocalizationArgs: Array<String>? get() = android.titleLocalizationArgs
        actual val titleLocalizationKey: String? get() = android.titleLocalizationKey
        actual val vibrateTimings: LongArray? get() = android.vibrateTimings
        actual val visibility: Int? get() = android.visibility
    }
    
    actual class Builder actual constructor(to: String) {
        private val android = AndroidRemoteMessage.Builder(to)
        actual fun setCollapseKey(collapseKey: String?): Builder { android.setCollapseKey(collapseKey); return this }
        actual fun setData(data: Map<String, String>): Builder { android.setData(data); return this }
        actual fun setMessageId(messageId: String): Builder { android.setMessageId(messageId); return this }
        actual fun setMessageType(messageType: String?): Builder { android.setMessageType(messageType); return this }
        actual fun setTtl(ttl: Int): Builder { android.setTtl(ttl); return this }
        actual fun addData(key: String, value: String?): Builder { android.addData(key, value); return this }
        actual fun clearData(): Builder { android.clearData(); return this }
        actual fun build(): RemoteMessage = RemoteMessage(android.build())
    }
}

actual abstract class FirebaseMessagingService : AndroidService() {
    override fun onMessageReceived(remoteMessage: AndroidRemoteMessage) {
        onKmpMessageReceived(RemoteMessage(remoteMessage))
    }
    actual open fun onMessageReceived(remoteMessage: RemoteMessage) {}
    private fun onKmpMessageReceived(remoteMessage: RemoteMessage) { onMessageReceived(remoteMessage) }
    actual open override fun onDeletedMessages() {}
    actual open override fun onMessageSent(msgId: String) {}
    actual open override fun onSendError(msgId: String, exception: Exception) {}
    actual open override fun onNewToken(token: String) {}
}

actual class MessagingException(override val message: String?, actual val code: Int) : Exception(message)
