package com.kizuna.backend.features.user

import java.util.UUID

interface ProfileSettingsView {
    val id: UUID
    val username: String
    val displayName: String
    val bio: String?
    val avatarUrl: String?
}
