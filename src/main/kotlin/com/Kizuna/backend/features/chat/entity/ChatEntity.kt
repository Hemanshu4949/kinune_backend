package com.kizuna.backend.features.chat.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

enum class ChatType {
    DIRECT, GROUP
}

@Entity
@Table(name = "chats")
class ChatEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // <-- Add this line
    @Column(name = "type", nullable = false)
    var type: ChatType = ChatType.DIRECT,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
)
