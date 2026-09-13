package com.Kizuna.backend.chat

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "chats")
class ChatEntity(
    @Id
    @Column(nullable = false, updatable = false)
    var id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var isGroup: Boolean = false,

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)
