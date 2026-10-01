package com.kizuna.backend.features.chat.controller

import com.kizuna.backend.features.chat.MediaStorageService
import com.kizuna.backend.features.chat.dto.MediaUploadDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/media")
class MediaController(
    private val mediaStorageService: MediaStorageService
) {

    @GetMapping("/upload-url")
    fun getUploadUrl(
        @RequestParam extension: String,
        @RequestParam contentType: String
    ): ResponseEntity<MediaUploadDto> {
        val dto = mediaStorageService.generatePreSignedUploadUrl(extension, contentType)
        return ResponseEntity.ok(dto)
    }
}
