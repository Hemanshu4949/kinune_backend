package com.Kizuna.backend.features.chat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID
import java.util.Optional

interface ChatRepository : JpaRepository<ChatEntity, UUID> {
    @Query(value = """
        SELECT c.* FROM chats c
        JOIN chat_participants cp1 ON c.id = cp1.chat_id
        JOIN chat_participants cp2 ON c.id = cp2.chat_id
        WHERE c.type = 'DIRECT' AND cp1.user_id = :userA AND cp2.user_id = :userB
    """, nativeQuery = true)
    fun findDirectChatBetweenUsers(@Param("userA") userA: UUID, @Param("userB") userB: UUID): Optional<ChatEntity>

    @Query(value = """
        SELECT 
            c.id AS chatId, 
            c.type AS type, 
            CASE 
                WHEN c.type = 'DIRECT' THEN other_p.display_name 
                ELSE c.title 
            END AS title,
            CASE 
                WHEN c.type = 'DIRECT' THEN other_p.avatar_url 
                ELSE c.avatar_url 
            END AS avatarUrl,
            (
                SELECT COUNT(m.id) 
                FROM messages m 
                WHERE m.chat_id = c.id 
                  AND (cp.last_read_message_id IS NULL OR m.id > cp.last_read_message_id)
            ) AS unreadCount,
            lm.content AS latestMessageContent, 
            lm.type AS latestMessageType
        FROM chats c
        JOIN chat_participants cp ON c.id = cp.chat_id
        LEFT JOIN LATERAL (
            SELECT u.display_name, u.avatar_url
            FROM chat_participants cp2
            JOIN users u ON cp2.user_id = u.id
            WHERE cp2.chat_id = c.id AND cp2.user_id != :userId
            LIMIT 1
        ) other_p ON c.type = 'DIRECT'
        LEFT JOIN LATERAL (
            SELECT content, type 
            FROM messages m2 
            WHERE m2.chat_id = c.id 
            ORDER BY m2.id DESC 
            LIMIT 1
        ) lm ON true
        WHERE cp.user_id = :userId
    """, nativeQuery = true)
    fun findChatSummariesForUser(@Param("userId") userId: UUID): List<ChatSummaryProjection>
}
// adding a comment for account checking
interface ChatSummaryProjection {
    fun getChatId(): UUID
    fun getType(): String
    fun getTitle(): String?
    fun getAvatarUrl(): String?
    fun getUnreadCount(): Int
    fun getLatestMessageContent(): String?
    fun getLatestMessageType(): String?
}
