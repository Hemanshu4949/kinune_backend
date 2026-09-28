package com.kizuna.backend.features.message

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, SYSTEM
}

@Entity
@Table(name = "messages")
class MessageEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID,

    @Column(name = "chat_id", nullable = false)
    val chatId: UUID,

    @Column(name = "sender_id")
    val senderId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: MessageType,

    @Column(name = "content")
    val content: String?,

    @Column(name = "media_url")
    val mediaUrl: String?,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    val metadata: Map<String, Any>?,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
