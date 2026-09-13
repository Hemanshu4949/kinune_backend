package com.kizuna.backend.user

import com.Kizuna.backend.user.UserEntity
import com.Kizuna.backend.user.UserRepository
import com.Kizuna.backend.user.dto.UserDto
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserById(id: Long): UserDto? {
        return userRepository.findById(id).map { it.toDto() }.orElse(null)
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    private fun UserEntity.toDto() = UserDto(
        id = id,
        phoneNumber = phoneNumber,
        displayName = displayName,
        avatarUrl = avatarUrl,
        isOnline = isOnline
    )
}
