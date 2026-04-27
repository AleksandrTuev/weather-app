package com.dev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Session {
    private UUID uuid;
    private int userId;
    private LocalDateTime expiresAt;
}
