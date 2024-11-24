package ru.sweetbun.becomeanyone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class VKCloudConfig {

    @Bean
    public S3Client s3Client(@Value("${vk-cloud.storage.region}") String region,
                             @Value("${vk-cloud.storage.access-key}") String accessKey,
                             @Value("${vk-cloud.storage.secret-key}") String secretKey,
                             @Value("${vk-cloud.storage.endpoint-url}") String endpointUrl) {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpointUrl))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(@Value("${vk-cloud.storage.region}") String region,
                                   @Value("${vk-cloud.storage.access-key}") String accessKey,
                                   @Value("${vk-cloud.storage.secret-key}") String secretKey,
                                   @Value("${vk-cloud.storage.endpoint-url}") String endpointUrl) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpointUrl)) // Указываем endpoint для VK Cloud
                .build();
    }
}
