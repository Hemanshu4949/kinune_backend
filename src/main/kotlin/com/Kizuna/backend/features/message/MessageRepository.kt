package com.Kizuna.backend.features.message

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MessageRepository : JpaRepository<MessageEntity, UUID> {
    @Query("SELECT m FROM MessageEntity m WHERE m.chatId = :chatId AND m.id < :cursor ORDER BY m.id DESC")
    fun findMessagesBeforeCursor(@Param("chatId") chatId: UUID, @Param("cursor") cursor: UUID, pageable: Pageable): List<MessageEntity>
}
