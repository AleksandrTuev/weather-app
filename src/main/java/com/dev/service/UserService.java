package com.dev.service;

import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.InvalidPasswordException;
import com.dev.exception.UserNotFoundException;
import com.dev.mapper.UserMapper;
import com.dev.model.User;
import com.dev.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

//    @Autowired
//    public UserService(SessionService sessionService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.sessionService = sessionService;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    public Cookie signIn(UserSignInDto userDto) {
        User user = findUserByUsername(userDto.getUsername());
        if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            log.info("[{}] authorization [id: {}]", LocalDateTime.now(), user.getId());
            return sessionService.createSession(user.getId());
        } else {
            throw new InvalidPasswordException("Invalid password");
        }
    }

    public Cookie signUp(UserSignUpDto userDto) {
        User user = userMapper.toUser(userDto);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        String newUserName = user.getUsername();
        user.setUsername(newUserName);

        int userId = userRepository.save(user);
        log.info("[{}] registration (id: {})", LocalDateTime.now(), userId);
        return sessionService.createSession(userId);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByName(username.toLowerCase()).orElseThrow(
               () -> new UserNotFoundException("User not found")
        );
    }
}
