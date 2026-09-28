package com.kizuna.backend.core.common.seeder

import com.kizuna.backend.features.chat.ChatService
import com.kizuna.backend.features.chat.ChatRepository
import com.kizuna.backend.features.chat.ChatParticipantRepository
import com.kizuna.backend.features.message.MessageEntity
import com.kizuna.backend.features.message.MessageRepository
import com.kizuna.backend.features.message.MessageType
import com.kizuna.backend.features.user.UserEntity
import com.kizuna.backend.features.user.UserRepository
import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Component
class DatabaseSeeder(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val messageRepository: MessageRepository,
    private val chatService: ChatService
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        // 1. Wipe old schema data (Order matters due to foreign keys)
        messageRepository.deleteAll()
        chatParticipantRepository.deleteAll()
        chatRepository.deleteAll()
        userRepository.deleteAll()

        // 2. Seed Mock Users
        val user1 = UserEntity(
            id = UUID.randomUUID(),
            username = "hemanshu",
            displayName = "Sojitra Hemanshu",
            isActive = true,
            isVerified = true,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val user2 = UserEntity(
            id = UUID.randomUUID(),
            username = "test_user",
            displayName = "Test Account",
            isActive = true,
            isVerified = false,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val savedUsers = userRepository.saveAll(listOf(user1, user2))
        val u1Id = savedUsers[0].id!!
        val u2Id = savedUsers[1].id!!

        // 3. Seed 1-on-1 Chat
        val chatId = chatService.getOrCreateDirectChat(u1Id, u2Id)

        // 4. Seed First Message (Using UUIDv7)
        val msg = MessageEntity(
            id = UuidCreator.getTimeOrderedEpoch(),
            chatId = chatId,
            senderId = u1Id,
            type = MessageType.TEXT,
            content = "Hey! Welcome to the new Kizuna architecture.",
            mediaUrl = null,
            metadata = null,
            createdAt = Instant.now()
        )
        messageRepository.save(msg)
        
        println("✅ Database Seeded Successfully with New Schema!")
        println("User 1 ID: $u1Id")
        println("User 2 ID: $u2Id")
    }
}
