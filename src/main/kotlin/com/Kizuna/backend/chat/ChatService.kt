package com.Kizuna.backend.chat

import com.Kizuna.backend.chat.dto.ChatSummaryDto
import com.Kizuna.backend.message.MessageEntity
import com.Kizuna.backend.message.MessageRepository
import com.Kizuna.backend.message.dto.MessageDto
import com.Kizuna.backend.user.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) {

    fun getChatSummaries(currentUserId: Long = 1L): List<ChatSummaryDto> {
        val chats = chatRepository.findAllByOrderByUpdatedAtDesc()
        val usersMap = userRepository.findAll().associateBy { it.displayName }

        return chats.map { chat ->
            val lastMsg: MessageEntity? = messageRepository.findTopByChatIdOrderByCreatedAtDesc(chat.id)
            val unreadCount = messageRepository.countByChatIdAndSenderIdNotAndIsReadFalse(chat.id, currentUserId)

            val relatedUser = if (!chat.isGroup) usersMap[chat.name] else null
            val isOnline = relatedUser?.isOnline ?: false
            val avatarUrl = relatedUser?.avatarUrl

            val isVoiceNote = lastMsg?.messageType == "VOICE"
            val lastMessageText = when {
                lastMsg == null -> ""
                isVoiceNote -> "Voice note (${lastMsg.voiceDuration ?: "0:00"})"
                lastMsg.messageType == "IMAGE" -> lastMsg.content.ifEmpty { "Photo" }
                else -> lastMsg.content
            }

            val timestampStr = lastMsg?.timestamp ?: formatInstantToTime(chat.updatedAt)
            val isReadByMe = lastMsg == null || lastMsg.senderId == currentUserId || lastMsg.isRead

            ChatSummaryDto(
                id = chat.id,
                name = chat.name,
                lastMessage = lastMessageText,
                timestamp = timestampStr,
                unreadCount = unreadCount,
                isOnline = isOnline,
                isVoiceNote = isVoiceNote,
                voiceDuration = lastMsg?.voiceDuration,
                isReadByMe = isReadByMe,
                avatarUrl = avatarUrl
            )
        }
    }

    fun getMessagesForChat(chatId: String, currentUserId: Long = 1L): List<MessageDto> {
        val messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)
        return messages.map { msg ->
            MessageDto(
                id = msg.id,
                chatId = msg.chatId,
                senderId = msg.senderId,
                content = msg.content,
                messageType = msg.messageType,
                mediaUrl = msg.mediaUrl,
                voiceDuration = msg.voiceDuration,
                timestamp = msg.timestamp,
                isRead = msg.isRead,
                isOutgoing = msg.senderId == currentUserId,
                createdAt = msg.createdAt
            )
        }
    }

    private fun formatInstantToTime(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
