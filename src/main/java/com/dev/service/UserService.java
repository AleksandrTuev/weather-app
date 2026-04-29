package com.dev.service;

import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
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
        }
        //todo подумать что выкидывать при провалившейся авторизации
        return null; //todo заглушка
    }

    public Cookie signUp(UserSignUpDto userDto) {
        User user = userMapper.toUser(userDto);
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        int userId = userRepository.save(user);
        log.info("[{}] registration (id: {})", LocalDateTime.now(), userId);
        return sessionService.createSession(userId);
    }

    public Cookie signOut(String sessionId) {
        //скорее всего это для сессии
        return sessionService.deleteSession(UUID.fromString(sessionId));
    }

    private User findUserByUsername(String username) {
        //todo возвращать Optional. Юзера с данным именем может и не быть
        return userRepository.findByName(username).get();
    }
}
