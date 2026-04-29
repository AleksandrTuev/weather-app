package com.dev.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDto {
    private String username;
    private String password;
    private String repeatPassword;
}
