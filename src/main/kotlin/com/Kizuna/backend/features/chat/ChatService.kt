package com.Kizuna.backend.features.chat

import com.Kizuna.backend.features.chat.dto.ChatSummaryDto
import com.Kizuna.backend.features.message.MessageRepository
import com.Kizuna.backend.features.message.dto.MessageDto
import com.Kizuna.backend.features.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getChatSummaries(userId: UUID): List<ChatSummaryDto> {
        return chatRepository.findChatSummariesForUser(userId).map { projection ->
            ChatSummaryDto(
                id = projection.getChatId(),
                type = projection.getType(),
                title = projection.getTitle() ?: "Unknown User",
                avatarUrl = projection.getAvatarUrl(),
                unreadCount = projection.getUnreadCount(),
                lastSnippet = projection.getLatestMessageContent(),
                lastMessageType = projection.getLatestMessageType()
            )
        }
    }

    @Transactional
    fun getOrCreateDirectChat(userA: UUID, userB: UUID): UUID {
        // 1. Prevent duplicate 1-on-1 rooms
        val existingChat = chatRepository.findDirectChatBetweenUsers(userA, userB)
        if (existingChat.isPresent) {
            return existingChat.get().id
        }

        if (!userRepository.existsById(userB)) {
            throw IllegalArgumentException("Target user does not exist.")
        }

        // 2. Create the Chat room (title and avatar left null for DIRECT)
        val newChat = ChatEntity(
            id = UUID.randomUUID(),
            type = ChatType.DIRECT,
            title = null,
            avatarUrl = null,
            createdAt = java.time.Instant.now()
        )
        val savedChat = chatRepository.save(newChat)

        // 3. Attach both participants
        val participantA = ChatParticipantEntity(
            chatId = savedChat.id,
            userId = userA,
            lastReadMessageId = null,
            joinedAt = java.time.Instant.now()
        )
        val participantB = ChatParticipantEntity(
            chatId = savedChat.id,
            userId = userB,
            lastReadMessageId = null,
            joinedAt = java.time.Instant.now()
        )
        chatParticipantRepository.saveAll(listOf(participantA, participantB))

        return savedChat.id
    }

    @Transactional
    fun getMessagesForChat(chatId: UUID, userId: UUID, cursor: UUID?, limit: Int): List<MessageDto> {
        val pageRequest = PageRequest.of(0, limit)
        val searchCursor = cursor ?: com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch()
        
        val messages = messageRepository.findMessagesBeforeCursor(chatId, searchCursor, pageRequest)

        if (cursor == null && messages.isNotEmpty()) {
            val newestMessageId = messages.first().id
            chatParticipantRepository.updateLastReadMessageId(chatId, userId, newestMessageId)
        }

        return messages.map { MessageDto.fromEntity(it, userId) }
    }

    @Transactional
    fun createGroupChat(title: String, avatarUrl: String?, creatorId: UUID, participantIds: Set<UUID>): UUID {
        // 1. Combine creator and participants (Set ensures no duplicates)
        val allUserIds = participantIds + creatorId
        
        // 2. Validate all users exist
        val existingUsers = userRepository.findAllById(allUserIds)
        if (existingUsers.size != allUserIds.size) {
            throw IllegalArgumentException("One or more provided users do not exist.")
        }

        // 3. Create the Group Chat
        val newChat = ChatEntity(
            id = UUID.randomUUID(),
            type = ChatType.GROUP,
            title = title,
            avatarUrl = avatarUrl,
            createdAt = java.time.Instant.now()
        )
        val savedChat = chatRepository.save(newChat)

        // 4. Attach all participants
        val participants = allUserIds.map { userId ->
            ChatParticipantEntity(
                chatId = savedChat.id,
                userId = userId,
                lastReadMessageId = null,
                joinedAt = java.time.Instant.now()
            )
        }
        chatParticipantRepository.saveAll(participants)

        return savedChat.id
    }
}
