package com.kizuna.backend.features.chat

import com.kizuna.backend.features.chat.dto.MediaUploadDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class MediaStorageService(
    private val s3Presigner: S3Presigner,
    @Value("\${backblaze.b2.bucket-name}") private val bucketName: String,
    @Value("\${backblaze.b2.endpoint}") private val endpoint: String
) {

    fun generatePreSignedUploadUrl(extension: String, contentType: String): MediaUploadDto {
        val uuid = UUID.randomUUID().toString()
        val key = "chat-media/$uuid.$extension"
        
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()
            
        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(putObjectRequest)
            .build()
            
        val presignedRequest = s3Presigner.presignPutObject(presignRequest)
        val presignedUrl = presignedRequest.url().toString()
        
        return MediaUploadDto(
            presignedUrl = presignedUrl,
            publicUrl = key,
            contentType = contentType
        )
    }

    fun generatePreSignedGetUrl(objectKey: String): String {
        val getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build()
            
        val presignRequest = software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofDays(7))
            .getObjectRequest(getObjectRequest)
            .build()
            
        val presignedRequest = s3Presigner.presignGetObject(presignRequest)
        return presignedRequest.url().toString()
    }
}
