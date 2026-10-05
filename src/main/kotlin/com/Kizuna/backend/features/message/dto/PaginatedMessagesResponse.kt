package com.kizuna.backend.features.message.dto

data class PaginatedMessagesResponse(
    val messages: List<MessageDto>,
    val olderCursor: String?,
    val newerCursor: String?,
    val hasMoreOlder: Boolean,
    val hasMoreNewer: Boolean
)
