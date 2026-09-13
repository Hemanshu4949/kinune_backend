package com.Kizuna.backend.features.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {

    // Returns only the 5 fields needed for a profile settings page
    fun findProfileViewById(id: UUID): Optional<ProfileSettingsView>
    
    // Standard lookup
    fun findByUsernameIgnoreCase(username: String): Optional<UserEntity>
}
