package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

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
    private long expirationTime;
}
