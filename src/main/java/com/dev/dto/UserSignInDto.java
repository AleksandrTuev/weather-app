package com.dev.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInDto {
    private String username;
    private String password;
}
