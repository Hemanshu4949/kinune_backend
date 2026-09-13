package com.Kizuna.backend.message.dto

import java.time.Instant

data class MessageDto(
    val id: String,
    val chatId: String,
    val senderId: Long,
    val content: String,
    val messageType: String,
    val mediaUrl: String? = null,
    val voiceDuration: String? = null,
    val timestamp: String,
    val isRead: Boolean,
    val isOutgoing: Boolean = false,
    val createdAt: Instant? = null
)
