package com.Kizuna.backend.user.dto

data class UserDto(
    val id: Long?,
    val phoneNumber: String,
    val displayName: String,
    val avatarUrl: String?,
    val isOnline: Boolean
)
