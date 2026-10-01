package com.kizuna.backend.core.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI


@Configuration
class BackblazeS3Config(
    @Value("\${backblaze.b2.key-id}") private val keyId: String,
    @Value("\${backblaze.b2.application-key}") private val applicationKey: String,
    @Value("\${backblaze.b2.endpoint}") private val endpoint: String,
    @Value("\${backblaze.b2.region}") private val regionString: String
) {
    
    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(keyId, applicationKey)
        val region = Region.of(regionString)
        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(region)
            .endpointOverride(URI.create(endpoint))
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(keyId, applicationKey)
        val region = Region.of(regionString)
        return S3Presigner.builder()
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(region)
            .endpointOverride(URI.create(endpoint))
            .build()
    }

}
