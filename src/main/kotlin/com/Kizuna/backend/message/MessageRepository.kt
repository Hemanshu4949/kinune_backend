package com.Kizuna.backend.message

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<MessageEntity, String> {
    fun findByChatIdOrderByCreatedAtAsc(chatId: String): List<MessageEntity>
    fun findTopByChatIdOrderByCreatedAtDesc(chatId: String): MessageEntity?
    fun countByChatIdAndSenderIdNotAndIsReadFalse(chatId: String, senderId: Long): Int
}
