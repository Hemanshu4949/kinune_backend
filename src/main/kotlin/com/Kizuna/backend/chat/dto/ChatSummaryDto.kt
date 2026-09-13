package com.Kizuna.backend.chat.dto

data class ChatSummaryDto(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val isVoiceNote: Boolean,
    val voiceDuration: String? = null,
    val isReadByMe: Boolean,
    val avatarUrl: String? = null
)
