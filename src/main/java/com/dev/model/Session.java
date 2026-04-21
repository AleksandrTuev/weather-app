package com.dev.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Session {
    private UUID uuid;
    private int userId;
    private LocalDateTime expiresAt;
}
