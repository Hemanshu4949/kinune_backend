package com.kizuna.backend.core.common.seeder

import com.kizuna.backend.features.chat.ChatService
import com.kizuna.backend.features.chat.Repository.ChatRepository
import com.kizuna.backend.features.chat.Repository.ChatParticipantRepository
import com.kizuna.backend.features.message.MessageEntity
import com.kizuna.backend.features.message.MessageRepository
import com.kizuna.backend.features.message.MessageType
import com.kizuna.backend.features.user.UserEntity
import com.kizuna.backend.features.user.UserRepository
import com.github.f4b6a3.uuid.UuidCreator
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Component
@ConditionalOnProperty(name = ["seeder.enabled"], havingValue = "true", matchIfMissing = false)
class DatabaseSeeder(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val messageRepository: MessageRepository,
    private val chatService: ChatService
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        val jeetId = UUID.fromString("c88f9123-88cc-4991-8b22-9834bc672222")
        val samId = UUID.fromString("b57e3248-74ee-4112-9c33-1249fa6b1111")
        val hemanshuId = UUID.fromString("4a6d2157-36dd-4268-ba45-860ba35b25d6")

        // 1. Seed Users
        if (userRepository.findById(jeetId).isEmpty) {
            val jeet = UserEntity(
                id = jeetId,
                username = "jeet",
                email = "jeet@example.com",
                displayName = "Jeet",
                isActive = true,
                isVerified = true
            )
            val sam = UserEntity(
                id = samId,
                username = "sam",
                email = "sam@example.com",
                displayName = "Sam",
                isActive = true,
                isVerified = true
            )
            val hemanshu = UserEntity(
                id = hemanshuId,
                username = "hemanshu",
                email = "hemanshu@example.com",
                displayName = "Hemanshu",
                isActive = true,
                isVerified = true
            )
            userRepository.saveAll(listOf(jeet, sam, hemanshu))
            println("✅ Users seeded successfully.")
        } else {
            println("ℹ️ Users already seeded.")
            return
        }

        // 2. Create a Group Chat
        val groupChatId = UUID.randomUUID()
        val groupChat = com.kizuna.backend.features.chat.entity.ChatEntity(
            id = groupChatId,
            type = com.kizuna.backend.features.chat.entity.ChatType.GROUP,
            title = "The Kizuna Team"
        )
        chatRepository.save(groupChat)

        // 3. Add Participants to the Group Chat
        val participants = listOf(
            com.kizuna.backend.features.chat.entity.ChatParticipantEntity(chatId = groupChatId, userId = jeetId),
            com.kizuna.backend.features.chat.entity.ChatParticipantEntity(chatId = groupChatId, userId = samId),
            com.kizuna.backend.features.chat.entity.ChatParticipantEntity(chatId = groupChatId, userId = hemanshuId)
        )
        chatParticipantRepository.saveAll(participants)

        // 4. Create some initial messages
        val messages = listOf(
            MessageEntity(
                id = UuidCreator.getTimeOrderedEpoch(),
                chatId = groupChatId,
                senderId = jeetId,
                type = MessageType.TEXT,
                content = "Hey everyone! Welcome to the new Kizuna chat.",
                mediaUrl = null,
                metadata = null
            ),
            MessageEntity(
                id = UuidCreator.getTimeOrderedEpoch(),
                chatId = groupChatId,
                senderId = samId,
                type = MessageType.TEXT,
                content = "Looks great, Jeet! Exciting times.",
                mediaUrl = null,
                metadata = null
            ),
            MessageEntity(
                id = UuidCreator.getTimeOrderedEpoch(),
                chatId = groupChatId,
                senderId = hemanshuId,
                type = MessageType.TEXT,
                content = "Let's build something amazing together.",
                mediaUrl = null,
                metadata = null
            )
        )
        messageRepository.saveAll(messages)

        println("✅ Database Seeded Successfully with New Data!")
    }
}
