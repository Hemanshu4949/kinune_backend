package com.Kizuna.backend.message.dto

data class SendMessagePayload(
    val chatId: String,
    val senderId: Long,
    val content: String,
    val messageType: String = "TEXT",
    val voiceDuration: String? = null,
    val mediaUrl: String? = null
)
