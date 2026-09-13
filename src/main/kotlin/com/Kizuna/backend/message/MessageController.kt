package com.kizuna.backend.message


import com.Kizuna.backend.message.dto.MessageDto
import com.Kizuna.backend.message.dto.SendMessagePayload
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class MessageController(
    private val messageService: MessageService
) {

    @MessageMapping("/chat.sendMessage")
    fun handleSendMessage(@Payload payload: SendMessagePayload): MessageDto {
        return messageService.sendMessage(payload, payload.senderId)
    }
}
