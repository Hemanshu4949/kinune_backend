package com.Kizuna.backend.message

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "messages",
    indexes = [Index(name = "idx_messages_chat_id", columnList = "chat_id")]
)
class MessageEntity(
    @Id
    @Column(nullable = false, updatable = false)
    var id: String = UUID.randomUUID().toString(),

    @Column(name = "chat_id", nullable = false)
    var chatId: String = "",

    @Column(name = "sender_id", nullable = false)
    var senderId: Long = 0L,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var messageType: String = "TEXT",

    @Column(nullable = true)
    var mediaUrl: String? = null,

    @Column(nullable = true)
    var voiceDuration: String? = null,

    @Column(nullable = false)
    var timestamp: String = "",

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
)
