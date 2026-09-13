package com.Kizuna.backend.chat

import com.Kizuna.backend.chat.dto.ChatSummaryDto
import com.Kizuna.backend.message.dto.MessageDto
import com.Kizuna.backend.message.dto.SendMessagePayload
import com.kizuna.backend.message.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = ["*"])
class ChatController(
    private val chatService: ChatService,
    private val messageService: MessageService
) {

    @GetMapping
    fun getChats(@RequestParam(defaultValue = "1") currentUserId: Long): ResponseEntity<List<ChatSummaryDto>> {
        val summaries = chatService.getChatSummaries(currentUserId)
        return ResponseEntity.ok(summaries)
    }

    @GetMapping("/{id}/messages")
    fun getMessages(
        @PathVariable id: String,
        @RequestParam(defaultValue = "1") currentUserId: Long
    ): ResponseEntity<List<MessageDto>> {
        val messages = chatService.getMessagesForChat(id, currentUserId)
        return ResponseEntity.ok(messages)
    }

    @PostMapping("/{id}/messages")
    fun postMessage(
        @PathVariable id: String,
        @RequestBody payload: SendMessagePayload,
        @RequestParam(defaultValue = "1") currentUserId: Long
    ): ResponseEntity<MessageDto> {
        val effectivePayload = if (payload.chatId.isEmpty()) {
            payload.copy(chatId = id)
        } else {
            payload
        }
        val createdMessage = messageService.sendMessage(effectivePayload, currentUserId)
        return ResponseEntity.ok(createdMessage)
    }
}
