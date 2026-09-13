package com.kizuna.backend.common.seeder


import com.Kizuna.backend.chat.ChatEntity
import com.Kizuna.backend.chat.ChatRepository
import com.Kizuna.backend.message.MessageEntity
import com.Kizuna.backend.message.MessageRepository
import com.Kizuna.backend.user.UserEntity
import com.Kizuna.backend.user.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DatabaseSeeder(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (userRepository.count() > 0L) {
            return
        }

        val selfUser = userRepository.save(
            UserEntity(
                id = 1L,
                displayName = "Rei Takahashi",
                phoneNumber = "+819011112222",
                isOnline = true
            )
        )
        val ren = userRepository.save(UserEntity(id = 2L, displayName = "Ren Takahashi", phoneNumber = "+819022223333", isOnline = true))
        val hana = userRepository.save(UserEntity(id = 3L, displayName = "Hana Tanaka", phoneNumber = "+819033334444", isOnline = true))
        val aoi = userRepository.save(UserEntity(id = 4L, displayName = "Aoi Studio", phoneNumber = "+819044445555", isOnline = false))
        val kaito = userRepository.save(UserEntity(id = 5L, displayName = "Kaito Moriyama", phoneNumber = "+819055556666", isOnline = false))
        val yuna = userRepository.save(UserEntity(id = 6L, displayName = "Yuna Kitsune", phoneNumber = "+819066667777", isOnline = true))
        val sora = userRepository.save(UserEntity(id = 7L, displayName = "Sora Sato", phoneNumber = "+819077778888", isOnline = false))

        val baseInstant = Instant.now()

        val chat1 = chatRepository.save(
            ChatEntity(
                id = "chat-ren-takahashi",
                name = "Ren Takahashi",
                isGroup = false,
                updatedAt = baseInstant.minusSeconds(60)
            )
        )
        val chat2 = chatRepository.save(ChatEntity(id = "chat-hana-haru", name = "Hana & Haru", isGroup = true, updatedAt = baseInstant.minusSeconds(120)))
        val chat3 = chatRepository.save(ChatEntity(id = "chat-aoi-studio", name = "Aoi Studio", isGroup = true, updatedAt = baseInstant.minusSeconds(180)))
        val chat4 = chatRepository.save(ChatEntity(id = "chat-kaito-moriyama", name = "Kaito Moriyama", isGroup = false, updatedAt = baseInstant.minusSeconds(240)))
        val chat5 = chatRepository.save(ChatEntity(id = "chat-yuna-kitsune", name = "Yuna Kitsune", isGroup = false, updatedAt = baseInstant.minusSeconds(300)))
        val chat6 = chatRepository.save(ChatEntity(id = "chat-sora-sato", name = "Sora Sato", isGroup = false, updatedAt = baseInstant.minusSeconds(360)))

        messageRepository.save(
            MessageEntity(
                chatId = chat1.id,
                senderId = ren.id!!,
                content = "Hey Rei, got a second?",
                messageType = "TEXT",
                timestamp = "17:35",
                isRead = false,
                createdAt = baseInstant.minusSeconds(100)
            )
        )
        messageRepository.save(MessageEntity(chatId = chat1.id, senderId = ren.id!!, content = "Did you check the release schedule?", messageType = "TEXT", timestamp = "17:36", isRead = false, createdAt = baseInstant.minusSeconds(80)))
        messageRepository.save(MessageEntity(chatId = chat1.id, senderId = ren.id!!, content = "Are you bringing the manga volum...", messageType = "TEXT", timestamp = "17:38", isRead = false, createdAt = baseInstant.minusSeconds(60)))

        messageRepository.save(MessageEntity(chatId = chat2.id, senderId = hana.id!!, content = "Hey! Did you check out the new manga cafe in Akiba? The art prints look insane! 🌸 ✨", messageType = "TEXT", timestamp = "17:42", isRead = true, createdAt = baseInstant.minusSeconds(200)))
        messageRepository.save(MessageEntity(chatId = chat2.id, senderId = selfUser.id!!, content = "Not yet!! You mean the one with the cyberpunk rooftop garden? I saw their reel!", messageType = "TEXT", timestamp = "17:43", isRead = true, createdAt = baseInstant.minusSeconds(180)))
        messageRepository.save(MessageEntity(chatId = chat2.id, senderId = hana.id!!, content = "Look at the view from table 4! They also give out holographic stamp cards", messageType = "IMAGE", mediaUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24", timestamp = "17:44", isRead = true, createdAt = baseInstant.minusSeconds(160)))
        messageRepository.save(MessageEntity(chatId = chat2.id, senderId = selfUser.id!!, content = "Voice note (0:24)", messageType = "VOICE", voiceDuration = "0:24", timestamp = "17:45", isRead = true, createdAt = baseInstant.minusSeconds(140)))
        messageRepository.save(MessageEntity(chatId = chat2.id, senderId = hana.id!!, content = "Voice note (0:14)", messageType = "VOICE", voiceDuration = "0:14", timestamp = "17:46", isRead = false, createdAt = baseInstant.minusSeconds(120)))

        messageRepository.save(MessageEntity(chatId = chat3.id, senderId = aoi.id!!, content = "Draft update 1", messageType = "TEXT", timestamp = "16:00", isRead = false, createdAt = baseInstant.minusSeconds(210)))
        messageRepository.save(MessageEntity(chatId = chat3.id, senderId = aoi.id!!, content = "Draft update 2", messageType = "TEXT", timestamp = "16:05", isRead = false, createdAt = baseInstant.minusSeconds(200)))
        messageRepository.save(MessageEntity(chatId = chat3.id, senderId = aoi.id!!, content = "Draft update 3", messageType = "TEXT", timestamp = "16:10", isRead = false, createdAt = baseInstant.minusSeconds(190)))
        messageRepository.save(MessageEntity(chatId = chat3.id, senderId = aoi.id!!, content = "Check out this sketch! 🎨 Draft v2 i...", messageType = "TEXT", timestamp = "16:15", isRead = false, createdAt = baseInstant.minusSeconds(180)))

        messageRepository.save(MessageEntity(chatId = chat4.id, senderId = kaito.id!!, content = "The arcade tournament was crazy h...", messageType = "TEXT", timestamp = "15:20", isRead = true, createdAt = baseInstant.minusSeconds(240)))
        messageRepository.save(MessageEntity(chatId = chat5.id, senderId = yuna.id!!, content = "Sent you the playlist link 🎧 Listen ...", messageType = "TEXT", timestamp = "14:10", isRead = true, createdAt = baseInstant.minusSeconds(300)))
        messageRepository.save(MessageEntity(chatId = chat6.id, senderId = sora.id!!, content = "Shared a file: kizuna-keyframe-04....", messageType = "TEXT", timestamp = "12:05", isRead = true, createdAt = baseInstant.minusSeconds(360)))
    }
}
