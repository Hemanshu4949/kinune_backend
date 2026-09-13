package com.Kizuna.backend.features.user

import com.Kizuna.backend.features.user.dto.UserDto
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserById(id: UUID): UserDto? {
        return userRepository.findById(id).map { it.toDto() }.orElse(null)
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    private fun UserEntity.toDto() = UserDto(
        id = id,
        username = username,
        phoneNumber = phoneNumber,
        displayName = displayName,
        avatarUrl = avatarUrl,
        isActive = isActive
    )
}
