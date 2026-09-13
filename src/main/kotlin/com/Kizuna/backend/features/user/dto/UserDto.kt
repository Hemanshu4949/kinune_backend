package com.Kizuna.backend.features.user.dto

import java.util.UUID

data class UserDto(
    val id: UUID?,
    val username: String,
    val phoneNumber: String?,
    val displayName: String,
    val avatarUrl: String?,
    val isActive: Boolean
)
