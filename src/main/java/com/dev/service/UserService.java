package com.dev.service;

import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.InvalidPasswordException;
import com.dev.exception.UserNotFoundException;
import com.dev.exception.UsernameAlreadyExistsException;
import com.dev.mapper.UserMapper;
import com.dev.model.User;
import com.dev.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public Cookie signIn(UserSignInDto userDto) {
        User user = findUserByUsername(userDto.getUsername());
        if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            log.info("Authorization [id: {}]", user.getId());
            return sessionService.createSession(user.getId());
        } else {
            log.error("Invalid password. User #{}", user.getId());
            throw new InvalidPasswordException("Invalid password");
        }
    }

    public Cookie signUp(UserSignUpDto userDto) {
        User user = userMapper.toUser(userDto);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        String newUserName = user.getUsername().toLowerCase();
        user.setUsername(newUserName);

        if (userRepository.findByName(newUserName).isPresent()) {
            log.error("User with name '{}' already exists", newUserName);
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        int userId = userRepository.save(user);
        log.info("Registration (id: {})", userId);
        return sessionService.createSession(userId);
    }

    public void signOut(String sessionId) {
        sessionService.deleteSession(UUID.fromString(sessionId));
    }

    private User findUserByUsername(String username) {
        log.info("Finding user");
        try {
            return userRepository.findByName(username.toLowerCase()).get();
        } catch (NoSuchElementException e) {
            log.error("User with name '{}' not found", username);
            throw new UserNotFoundException("User not found");
        }
    }
}
