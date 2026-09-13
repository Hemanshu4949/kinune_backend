package com.Kizuna.backend.features.chat

import com.Kizuna.backend.features.chat.dto.ChatSummaryDto
import com.Kizuna.backend.features.chat.dto.CreateGroupPayload
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = ["*"])
class ChatController(
    private val chatService: ChatService
) {

    @GetMapping
    fun getChats(@RequestParam currentUserId: UUID): ResponseEntity<List<ChatSummaryDto>> {
        val summaries = chatService.getChatSummaries(currentUserId)
        return ResponseEntity.ok(summaries)
    }

    @PostMapping("/direct")
    fun getOrCreateDirectChat(
        @RequestParam currentUserId: UUID,
        @RequestParam targetUserId: UUID
    ): ResponseEntity<Map<String, UUID>> {
        val chatId = chatService.getOrCreateDirectChat(currentUserId, targetUserId)
        return ResponseEntity.ok(mapOf("chatId" to chatId))
    }

    @PostMapping("/group")
    fun createGroupChat(
        @RequestParam currentUserId: UUID,
        @RequestBody payload: CreateGroupPayload
    ): ResponseEntity<Map<String, UUID>> {
        val chatId = chatService.createGroupChat(
            title = payload.title,
            avatarUrl = payload.avatarUrl,
            creatorId = currentUserId,
            participantIds = payload.participantIds
        )
        return ResponseEntity.ok(mapOf("chatId" to chatId))
    }
}
