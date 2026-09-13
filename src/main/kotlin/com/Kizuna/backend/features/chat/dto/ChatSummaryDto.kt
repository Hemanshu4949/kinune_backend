package com.Kizuna.backend.features.chat.dto

import java.util.UUID

data class ChatSummaryDto(
    val id: UUID,
    val type: String,
    val title: String,
    val avatarUrl: String?,
    val unreadCount: Int,
    val lastSnippet: String?,
    val lastMessageType: String?
)
