package com.Kizuna.backend.message


import com.Kizuna.backend.chat.ChatRepository
import com.Kizuna.backend.message.MessageEntity
import com.Kizuna.backend.message.MessageRepository
import com.Kizuna.backend.message.dto.MessageDto
import com.Kizuna.backend.message.dto.SendMessagePayload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @Transactional
    fun sendMessage(payload: SendMessagePayload, currentUserId: Long = 1L): MessageDto {
        val now = Instant.now()
        val timeStr = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(now)

        val entity = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = payload.chatId,
            senderId = payload.senderId,
            content = payload.content,
            messageType = payload.messageType,
            mediaUrl = payload.mediaUrl,
            voiceDuration = payload.voiceDuration,
            timestamp = timeStr,
            isRead = false,
            createdAt = now
        )

        val saved = messageRepository.save(entity)

        chatRepository.findById(payload.chatId).ifPresent { chat ->
            chat.updatedAt = now
            chatRepository.save(chat)
        }

        val dto = MessageDto(
            id = saved.id,
            chatId = saved.chatId,
            senderId = saved.senderId,
            content = saved.content,
            messageType = saved.messageType,
            mediaUrl = saved.mediaUrl,
            voiceDuration = saved.voiceDuration,
            timestamp = saved.timestamp,
            isRead = saved.isRead,
            isOutgoing = saved.senderId == currentUserId,
            createdAt = saved.createdAt
        )

        messagingTemplate.convertAndSend("/topic/chat/${saved.chatId}", dto)

        return dto
    }
}
