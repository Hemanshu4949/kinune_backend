package com.Kizuna.backend.features.message

import com.Kizuna.backend.features.chat.ChatService
import com.Kizuna.backend.features.message.dto.MessageDto
import com.Kizuna.backend.features.message.dto.SendMessagePayload
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
    // GET /api/messages/chat/123-456?currentUserId=789&cursor=...
    @GetMapping("/api/messages/chat/{chatId}")
    fun getMessageHistory(
        @PathVariable chatId: UUID,
        @RequestParam currentUserId: UUID,
        @RequestParam(required = false) cursor: UUID?,
        @RequestParam(defaultValue = "40") limit: Int
    ): ResponseEntity<List<MessageDto>> {
        // Calls ChatService because it handles the unread watermark reset logic
        val messages = chatService.getMessagesForChat(chatId, currentUserId, cursor, limit)
        return ResponseEntity.ok(messages)
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
