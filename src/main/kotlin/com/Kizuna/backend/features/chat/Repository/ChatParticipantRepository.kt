package com.kizuna.backend.features.chat.Repository

import com.kizuna.backend.features.chat.dto.ChatParticipantProjection
import com.kizuna.backend.features.chat.entity.ChatParticipantEntity
import com.kizuna.backend.features.chat.entity.ChatParticipantId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChatParticipantRepository : JpaRepository<ChatParticipantEntity, ChatParticipantId> {
    @Modifying
    @Query(
        "UPDATE ChatParticipantEntity cp SET cp.lastReadMessageId = :lastReadMessageId " +
        "WHERE cp.chatId = :chatId AND cp.userId = :userId"
    )
    fun updateLastReadMessageId(
        @Param("chatId") chatId: UUID,
        @Param("userId") userId: UUID,
        @Param("lastReadMessageId") lastReadMessageId: UUID
    ): Int

    @Query("""
        SELECT new com.kizuna.backend.features.chat.dto.ChatParticipantProjection(
            cp.chatId, u.id, u.displayName
        )
        FROM ChatParticipantEntity cp
        JOIN UserEntity u ON cp.userId = u.id
        WHERE cp.chatId IN :chatIds
    """)
    fun findParticipantsForChats(@Param("chatIds") chatIds: List<UUID>): List<ChatParticipantProjection>
}
