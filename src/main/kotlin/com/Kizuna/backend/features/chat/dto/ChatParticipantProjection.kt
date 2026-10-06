package com.kizuna.backend.features.chat.dto

import java.util.UUID

data class ChatParticipantProjection(
    val chatId: UUID,
    val userId: UUID,
    val displayName: String
)
