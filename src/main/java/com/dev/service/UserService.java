package com.dev.service;

import com.dev.model.User;
import com.dev.repository.UserRepository;
import com.dev.util.CreatorCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final SessionService sessionService;

    @Autowired
    public UserService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void signIn(String userName, String password, HttpServletRequest req, HttpServletResponse resp) {
        User user = findUserByUsername(userName);
        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            CreatorCookie.create("user_id", String.valueOf(user.getId()), resp);
            sessionService.createSession(req, resp, user.getId());
            log.info("Пользователь с ID: {} авторизовался", user.getId());
        }
    }

    public void signUp(User user, HttpServletRequest req, HttpServletResponse resp) {
        String hashedPassword = encode(user.getPassword());
        user.setPassword(hashedPassword);
        int userId = UserRepository.save(user);
        CreatorCookie.create("user_id", String.valueOf(userId), resp);
        sessionService.createSession(req, resp, userId);
        log.info("Пользователь с ID: {} зарегистрировался", userId);
    }

    public void signOut(HttpServletRequest req, HttpServletResponse res) {
        //скорее всего это для сессии

    }

    private User findUserByUsername(String username) {
        //todo возвращать Optional. Юзера с данным именем может и не быть
        return UserRepository.findByName(username).get();
    }

    private String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
