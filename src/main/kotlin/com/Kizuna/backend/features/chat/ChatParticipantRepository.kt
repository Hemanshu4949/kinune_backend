package com.Kizuna.backend.features.chat

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
}
