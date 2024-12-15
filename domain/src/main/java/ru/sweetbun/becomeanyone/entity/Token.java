package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Token")
public class Token {
    @Id
    private String id;
    private String username;
    private String status;
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expirationTime;
}
