package com.kizuna.backend.features.chat

import com.kizuna.backend.features.chat.dto.ChatSummaryDto
import com.kizuna.backend.features.message.MessageRepository
import com.kizuna.backend.features.message.dto.MessageDto
import com.kizuna.backend.features.user.UserRepository
import com.kizuna.backend.features.chat.Repository.ChatParticipantRepository
import com.kizuna.backend.features.chat.Repository.ChatRepository
import com.kizuna.backend.features.chat.entity.ChatEntity
import com.kizuna.backend.features.chat.entity.ChatParticipantEntity
import com.kizuna.backend.features.chat.entity.ChatType
import com.kizuna.backend.features.message.MessageEntity
import com.kizuna.backend.features.message.dto.PaginatedMessagesResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val mediaStorageService: MediaStorageService
) {

    @Transactional(readOnly = true)
    fun getChatSummaries(userId: UUID): List<ChatSummaryDto> {
        val projections = chatRepository.findChatSummariesForUser(userId)
        if (projections.isEmpty()) return emptyList()

        val chatIds = projections.map { it.getChatId() }
        val participantProjections = chatParticipantRepository.findParticipantsForChats(chatIds)

        val participantsByChatId = participantProjections.groupBy({ it.chatId }) { proj ->
            com.kizuna.backend.features.chat.dto.ParticipantSummaryDto(proj.userId, proj.displayName)
        }

        return projections.map { projection ->
            ChatSummaryDto(
                id = projection.getChatId(),
                type = projection.getType(),
                title = projection.getTitle() ?: "Unknown User",
                avatarUrl = projection.getAvatarUrl(),
                unreadCount = projection.getUnreadCount(),
                lastSnippet = projection.getLatestMessageContent(),
                lastMessageType = projection.getLatestMessageType(),
                participants = participantsByChatId[projection.getChatId()] ?: emptyList()
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
            createdAt = Instant.now()
        )
        val savedChat = chatRepository.save(newChat)

        // 3. Attach both participants
        val participantA = ChatParticipantEntity(
            chatId = savedChat.id,
            userId = userA,
            lastReadMessageId = null,
            joinedAt = Instant.now()
        )
        val participantB = ChatParticipantEntity(
            chatId = savedChat.id,
            userId = userB,
            lastReadMessageId = null,
            joinedAt = Instant.now()
        )
        chatParticipantRepository.saveAll(listOf(participantA, participantB))

        return savedChat.id
    }

    @Transactional
    fun getMessagesForChat(
        chatId: UUID, 
        userId: UUID, 
        aroundMessageId: UUID?, 
        beforeCursor: UUID?, 
        afterCursor: UUID?, 
        limit: Int
    ): PaginatedMessagesResponse {
        var messages = emptyList<MessageEntity>()
        var hasMoreOlder = false
        var hasMoreNewer = false

        when {
            aroundMessageId != null -> {
                val targetMessage = messageRepository.findById(aroundMessageId)
                if (targetMessage.isPresent) {
                    val createdAt = targetMessage.get().createdAt
                    val olderLimit = limit / 2
                    val newerLimit = limit - olderLimit
                    
                    val older = messageRepository.findOlderMessages(chatId, createdAt, PageRequest.of(0, olderLimit + 1))
                    val newer = messageRepository.findNewerMessages(chatId, createdAt, PageRequest.of(0, newerLimit + 1))
                    
                    hasMoreOlder = older.size > olderLimit
                    hasMoreNewer = newer.size > newerLimit
                    
                    val olderList = older.take(olderLimit).reversed()
                    val newerList = newer.take(newerLimit)
                    // Ensure the target message is included in olderList if we used <=, but since we used <= it might be the first element.
                    // Wait, we defined findOlderMessages as <= in the previous step, so it INCLUDES the target message.
                    messages = olderList + newerList
                }
            }
            beforeCursor != null -> {
                val targetMessage = messageRepository.findById(beforeCursor)
                if (targetMessage.isPresent) {
                    // To fetch older, we must ensure we don't include the cursor itself if findOlderMessages uses <=.
                    // But actually, we want strict < for pagination. Let's just filter it out in memory.
                    val older = messageRepository.findOlderMessages(chatId, targetMessage.get().createdAt, PageRequest.of(0, limit + 2))
                    val filteredOlder = older.filter { it.id != beforeCursor }
                    hasMoreOlder = filteredOlder.size > limit
                    messages = filteredOlder.take(limit).reversed()
                    hasMoreNewer = true 
                }
            }
            afterCursor != null -> {
                val targetMessage = messageRepository.findById(afterCursor)
                if (targetMessage.isPresent) {
                    val newer = messageRepository.findNewerMessages(chatId, targetMessage.get().createdAt, PageRequest.of(0, limit + 1))
                    hasMoreNewer = newer.size > limit
                    messages = newer.take(limit)
                    hasMoreOlder = true 
                }
            }
            else -> {
                // Default: fetch latest
                val older = messageRepository.findOlderMessages(chatId, Instant.now(), PageRequest.of(0, limit + 1))
                hasMoreOlder = older.size > limit
                messages = older.take(limit).reversed()
                hasMoreNewer = false
            }
        }

        if (beforeCursor == null && aroundMessageId == null && afterCursor == null && messages.isNotEmpty()) {
            val newestMessageId = messages.last().id
            chatParticipantRepository.updateLastReadMessageId(chatId, userId, newestMessageId)
        }

        val messageDtos = messages.map { MessageDto.fromEntity(it, userId, mediaStorageService) }
        
        return PaginatedMessagesResponse(
            messages = messageDtos,
            olderCursor = if (messages.isNotEmpty()) messages.first().id.toString() else null,
            newerCursor = if (messages.isNotEmpty()) messages.last().id.toString() else null,
            hasMoreOlder = hasMoreOlder,
            hasMoreNewer = hasMoreNewer
        )
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
            createdAt = Instant.now()
        )
        val savedChat = chatRepository.save(newChat)

        // 4. Attach all participants
        val participants = allUserIds.map { userId ->
            ChatParticipantEntity(
                chatId = savedChat.id,
                userId = userId,
                lastReadMessageId = null,
                joinedAt = Instant.now()
            )
        }
        chatParticipantRepository.saveAll(participants)

        return savedChat.id
    }

    @Transactional
    fun updateReadReceipt(chatId: UUID, userId: UUID, lastSeenMessageId: UUID) {
        val targetMessage = messageRepository.findById(lastSeenMessageId).orElseThrow { IllegalArgumentException("Message not found") }
        val participant = chatParticipantRepository.findById(com.kizuna.backend.features.chat.entity.ChatParticipantId(chatId, userId))
            .orElseThrow { IllegalArgumentException("User is not a participant in this chat") }

        val currentReadMessageId = participant.lastReadMessageId
        var shouldUpdate = false

        if (currentReadMessageId == null) {
            shouldUpdate = true
        } else {
            val currentReadMessage = messageRepository.findById(currentReadMessageId)
            if (currentReadMessage.isPresent && targetMessage.createdAt.isAfter(currentReadMessage.get().createdAt)) {
                shouldUpdate = true
            }
        }

        if (shouldUpdate) {
            chatParticipantRepository.updateLastReadMessageId(chatId, userId, lastSeenMessageId)
        }
    }
}
