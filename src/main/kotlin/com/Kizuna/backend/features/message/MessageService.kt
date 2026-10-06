package com.kizuna.backend.features.message

import com.kizuna.backend.features.message.dto.MessageDto
import com.kizuna.backend.features.message.dto.SendMessagePayload
import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

import java.util.concurrent.CompletableFuture

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val messagingTemplate: SimpMessagingTemplate,
    private val mediaStorageService: com.kizuna.backend.features.chat.MediaStorageService,
    private val chatParticipantRepository: com.kizuna.backend.features.chat.Repository.ChatParticipantRepository,
    private val userRepository: com.kizuna.backend.features.user.UserRepository,
    private val pushNotificationService: com.kizuna.backend.features.notification.PushNotificationService
) {
    @Transactional
    fun sendMessage(payload: SendMessagePayload, currentUserId: UUID): MessageDto {
        val message = MessageEntity(
            id = UuidCreator.getTimeOrderedEpoch(),
            chatId = payload.chatId,
            senderId = currentUserId,
            type = payload.type ?: MessageType.TEXT,
            content = payload.content,
            mediaUrl = payload.mediaUrl,
            metadata = payload.metadata,
            createdAt = Instant.now()
        )

        val savedMessage = messageRepository.save(message)
        val messageDto = MessageDto.fromEntity(savedMessage, currentUserId, mediaStorageService)

        messagingTemplate.convertAndSend("/topic/chat.${payload.chatId}", messageDto)
        
        CompletableFuture.runAsync {
            try {
                val participants = chatParticipantRepository.findParticipantsForChats(listOf(payload.chatId))
                val sender = userRepository.findById(currentUserId).orElse(null)
                val senderName = sender?.displayName ?: "Someone"
                
                participants.forEach { participant ->
                    if (participant.userId != currentUserId) {
                        val recipient = userRepository.findById(participant.userId).orElse(null)
                        recipient?.fcmToken?.let { token ->
                            val preview = payload.content ?: "Sent an attachment"
                            pushNotificationService.sendChatNotification(token, senderName, preview, payload.chatId.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore background task errors to not crash
            }
        }

        return messageDto
    }
}
