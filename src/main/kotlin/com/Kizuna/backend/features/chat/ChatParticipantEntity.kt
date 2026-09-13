package com.Kizuna.backend.features.chat

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant
import java.util.UUID

data class ChatParticipantId(
    var chatId: UUID? = null,
    var userId: UUID? = null
) : Serializable

@Entity
@Table(name = "chat_participants")
@IdClass(ChatParticipantId::class)
class ChatParticipantEntity(
    @Id
    @Column(name = "chat_id", nullable = false, updatable = false)
    val chatId: UUID,

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,

    @Column(name = "last_read_message_id")
    var lastReadMessageId: UUID? = null,

    @Column(name = "joined_at", nullable = false, updatable = false)
    val joinedAt: Instant = Instant.now()
)
