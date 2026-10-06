package com.kizuna.backend.features.message

import com.kizuna.backend.features.chat.ChatService
import com.kizuna.backend.features.message.dto.MessageDto
import com.kizuna.backend.features.message.dto.PaginatedMessagesResponse
import com.kizuna.backend.features.message.dto.SendMessagePayload
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class MessageController(
    private val messageService: MessageService,
    private val chatService: ChatService // Injected to trigger watermark resets during history fetch
) {

    // 1. REST: Fetch Message History via Keyset Pagination
    @GetMapping("/api/messages/chat/{chatId}")
    fun getMessageHistory(
        @PathVariable chatId: UUID,
        @RequestParam currentUserId: UUID,
        @RequestParam(required = false) aroundMessageId: UUID?,
        @RequestParam(required = false) beforeCursor: UUID?,
        @RequestParam(required = false) afterCursor: UUID?,
        @RequestParam(defaultValue = "40") limit: Int
    ): ResponseEntity<PaginatedMessagesResponse> {
        val response = chatService.getMessagesForChat(chatId, currentUserId, aroundMessageId, beforeCursor, afterCursor, limit)
        return ResponseEntity.ok(response)
    }

    // 2. REST: Fallback HTTP endpoint for sending messages (Useful for Postman testing)
    // POST /api/messages
    @PostMapping("/api/messages")
    fun sendMessageRest(@RequestBody payload: SendMessagePayload): ResponseEntity<MessageDto> {
        // Assuming senderId is included in the payload from the client
        val savedMessage = messageService.sendMessage(payload, payload.senderId)
        return ResponseEntity.ok(savedMessage)
    }

    // 3. WEBSOCKET: Real-Time STOMP endpoint
    @MessageMapping("/chat.sendMessage")
    fun handleWebSocketMessage(@Payload payload: SendMessagePayload) {
        // Processes the message, generates UUIDv7, saves to DB, and broadcasts via SimpMessagingTemplate
        messageService.sendMessage(payload, payload.senderId)
    }
}
