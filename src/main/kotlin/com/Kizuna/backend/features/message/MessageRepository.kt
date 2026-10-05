package com.kizuna.backend.features.message

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MessageRepository : JpaRepository<MessageEntity, UUID> {
    @Query("SELECT m FROM MessageEntity m WHERE m.chatId = :chatId AND m.createdAt <= :beforeCreatedAt ORDER BY m.createdAt DESC")
    fun findOlderMessages(@Param("chatId") chatId: UUID, @Param("beforeCreatedAt") beforeCreatedAt: java.time.Instant, pageable: Pageable): List<MessageEntity>

    @Query("SELECT m FROM MessageEntity m WHERE m.chatId = :chatId AND m.createdAt > :afterCreatedAt ORDER BY m.createdAt ASC")
    fun findNewerMessages(@Param("chatId") chatId: UUID, @Param("afterCreatedAt") afterCreatedAt: java.time.Instant, pageable: Pageable): List<MessageEntity>
}
