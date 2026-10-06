package com.kizuna.backend.features.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PushNotificationService {
    private val logger = LoggerFactory.getLogger(PushNotificationService::class.java)

    fun sendChatNotification(recipientToken: String, senderName: String, messagePreview: String, chatId: String) {
        try {
            val notification = Notification.builder()
                .setTitle(senderName)
                .setBody(messagePreview)
                .build()

            val message = Message.builder()
                .setToken(recipientToken)
                .setNotification(notification)
                .putData("chatId", chatId)
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Successfully sent message: $response")
        } catch (e: Exception) {
            logger.error("Error sending FCM notification", e)
        }
    }
}
