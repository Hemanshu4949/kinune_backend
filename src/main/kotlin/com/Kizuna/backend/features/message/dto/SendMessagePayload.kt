package com.Kizuna.backend.features.message.dto

import com.Kizuna.backend.features.message.MessageType
import java.util.UUID

data class SendMessagePayload(
    val chatId: UUID,
    val senderId: UUID,
    val type: MessageType?,
    val content: String?,
    val mediaUrl: String?,
    val metadata: Map<String, Any>?
)
