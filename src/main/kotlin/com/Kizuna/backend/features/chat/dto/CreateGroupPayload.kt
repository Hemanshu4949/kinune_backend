package com.kizuna.backend.features.chat.dto

import java.util.UUID

data class CreateGroupPayload(
    val title: String,
    val avatarUrl: String?,
    val participantIds: Set<UUID>
)
