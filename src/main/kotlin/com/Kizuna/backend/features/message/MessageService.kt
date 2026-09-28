package com.kizuna.backend.features.message

import com.kizuna.backend.features.message.dto.MessageDto
import com.kizuna.backend.features.message.dto.SendMessagePayload
import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val messagingTemplate: SimpMessagingTemplate // <-- Injected for WebSockets
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
        val messageDto = MessageDto.fromEntity(savedMessage, currentUserId)

        // Dispatch real-time WebSocket event to the specific chat room's topic
        messagingTemplate.convertAndSend("/topic/chat.${payload.chatId}", messageDto)

        return messageDto
    }
}
