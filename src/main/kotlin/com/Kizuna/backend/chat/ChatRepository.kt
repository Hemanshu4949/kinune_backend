package com.Kizuna.backend.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<ChatEntity, String> {
    fun findAllByOrderByUpdatedAtDesc(): List<ChatEntity>
}
