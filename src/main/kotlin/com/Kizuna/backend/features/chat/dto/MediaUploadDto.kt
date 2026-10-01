package com.kizuna.backend.features.chat.dto

data class MediaUploadDto(
    val presignedUrl: String,
    val publicUrl: String,
    val contentType: String
)
