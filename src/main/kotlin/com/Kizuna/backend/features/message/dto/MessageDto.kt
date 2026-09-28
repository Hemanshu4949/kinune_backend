package com.kizuna.backend.features.message.dto

import com.kizuna.backend.features.message.MessageEntity
import com.kizuna.backend.features.message.MessageType
import java.time.Instant
import java.util.UUID

data class MessageDto(
    val id: UUID,
    val chatId: UUID,
    val senderId: UUID?,
    val type: MessageType,
    val content: String?,
    val mediaUrl: String?,
    val metadata: Map<String, Any>?,
    val createdAt: Instant,
    val isOutgoing: Boolean
) {
    companion object {
        fun fromEntity(entity: MessageEntity, currentUserId: UUID): MessageDto {
            return MessageDto(
                id = entity.id,
                chatId = entity.chatId,
                senderId = entity.senderId,
                type = entity.type,
                content = entity.content,
                mediaUrl = entity.mediaUrl,
                metadata = entity.metadata,
                createdAt = entity.createdAt,
                isOutgoing = entity.senderId == currentUserId
            )
        }
    }
}
