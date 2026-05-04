package com.dev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.dev.util.ProjectConstants.*;
import static com.dev.util.ProjectConstants.MAX_PASSWORD_LENGTH;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDto {
    @NotBlank(message = "The name must not be empty")
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH,
            message = "The name length must be between {min} to {max} characters")
    @Pattern(regexp = "^\\p{L}+$", message = "Invalid name")
    private String username;

    @NotBlank(message = "The password must not be empty")
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH,
            message = "The password length must be between {min} to {max} characters")
    @Pattern(regexp = "^\\S+$", message = "Invalid password")
    private String password;

    @NotBlank(message = "The repeat password must not be empty")
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH,
            message = "The password length must be between {min} to {max} characters")
    @Pattern(regexp = "^\\S+$", message = "Invalid password")
    private String repeatPassword;
}
