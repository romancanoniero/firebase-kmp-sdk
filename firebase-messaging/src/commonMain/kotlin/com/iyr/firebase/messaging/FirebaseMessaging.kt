package com.iyr.firebase.messaging

import kotlinx.coroutines.flow.Flow

expect class FirebaseMessaging {
    companion object {
        fun getInstance(): FirebaseMessaging
    }
    
    suspend fun getToken(): String
    suspend fun deleteToken()
    fun subscribeToTopic(topic: String)
    fun unsubscribeFromTopic(topic: String)
    var isAutoInitEnabled: Boolean
    var deliveryMetricsExportToBigQueryEnabled: Boolean
    var isNotificationDelegationEnabled: Boolean
}

expect class RemoteMessage {
    val collapseKey: String?
    val data: Map<String, String>
    val from: String?
    val messageId: String?
    val messageType: String?
    val notification: Notification?
    val originalPriority: Int
    val priority: Int
    val sentTime: Long
    val to: String?
    val ttl: Int
    
    class Notification {
        val body: String?
        val bodyLocalizationArgs: Array<String>?
        val bodyLocalizationKey: String?
        val channelId: String?
        val clickAction: String?
        val color: String?
        val defaultLightSettings: Boolean
        val defaultSound: Boolean
        val defaultVibrateSettings: Boolean
        val icon: String?
        val imageUrl: String?
        val lightSettings: IntArray?
        val link: String?
        val localOnly: Boolean
        val notificationCount: Int?
        val notificationPriority: Int?
        val sound: String?
        val sticky: Boolean
        val tag: String?
        val ticker: String?
        val title: String?
        val titleLocalizationArgs: Array<String>?
        val titleLocalizationKey: String?
        val vibrateTimings: LongArray?
        val visibility: Int?
    }
    
    class Builder(to: String) {
        fun setCollapseKey(collapseKey: String?): Builder
        fun setData(data: Map<String, String>): Builder
        fun setMessageId(messageId: String): Builder
        fun setMessageType(messageType: String?): Builder
        fun setTtl(ttl: Int): Builder
        fun addData(key: String, value: String?): Builder
        fun clearData(): Builder
        fun build(): RemoteMessage
    }
}

// Common interface for message handling - to be implemented per platform
expect abstract class FirebaseMessagingService {
    open fun onMessageReceived(remoteMessage: RemoteMessage)
    open fun onDeletedMessages()
    open fun onMessageSent(msgId: String)
    open fun onSendError(msgId: String, exception: Exception)
    open fun onNewToken(token: String)
}

expect class MessagingException : Exception {
    val code: Int
}
