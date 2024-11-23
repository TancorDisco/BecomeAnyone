package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash("Token")
public class Token {
    @Id
    private String id;
    private String status;
    private long expirationTime;
}
