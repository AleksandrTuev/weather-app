import com.dev.config.LiquibaseConfig;
import com.dev.config.SpringConfig;
import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.InvalidPasswordException;
import com.dev.exception.UserNotFoundException;
import com.dev.exception.UsernameAlreadyExistsException;
import com.dev.mapper.UserMapper;
import com.dev.model.User;
import com.dev.repository.UserRepository;
import com.dev.service.UserService;
import com.dev.util.ProjectConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringConfig.class, LiquibaseConfig.class })
@WebAppConfiguration
@Transactional
public class UserServiceIntegrationTest {
    private static final String USERNAME = "ivan";
    private static final String PASSWORD = "0123456789";

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSignUp(){
        Cookie cookie = userService.signUp(createUserSignUpDto());

        assertNotNull(cookie);
        assertEquals(ProjectConstants.SESSION_ID, cookie.getName());
        assertNotNull(cookie.getValue());
        assertEquals(1, getCountUsers());
        assertEquals(1, getCountSessions());
    }

    @Test
    void testLogIn(){
        User user = createUser();
        UserSignInDto userSignInDto = userMapper.toUserSignInDto(user);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
        Cookie cookie = userService.signIn(userSignInDto);

        assertEquals(user.getUsername(), userRepository.findByName(user.getUsername()).get().getUsername());
        assertNotNull(cookie);
        assertEquals(1, getCountSessions());
    }

    @Test
    void shouldThrowUsernameAlreadyExistsException_WhenNonUniqueUserRegistration() {
        userService.signUp(createUserSignUpDto());
        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.signUp(createUserSignUpDto());
        });
    }

    @Test
    void shouldThrowInvalidPasswordException_WhenPasswordIsNotValid() {
        Cookie cookie = userService.signUp(createUserSignUpDto());
        userService.signOut(cookie.getValue());
        UserSignInDto user = createUserSignInDto();
        user.setPassword("123");

        Assertions.assertThrows(InvalidPasswordException.class, () -> {
            userService.signIn(user);
        });
    }

    @Test
    void shouldThrowUserNotFoundException_WhenUserIsNotRegistered() {
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.signIn(createUserSignInDto());
        });
    }

    private User createUser() {
        return new User(null, USERNAME, PASSWORD);
    }

    private UserSignInDto createUserSignInDto() {
        return new UserSignInDto(USERNAME, PASSWORD);
    }

    private UserSignUpDto createUserSignUpDto() {
        return new UserSignUpDto(USERNAME, PASSWORD, PASSWORD);
    }

    private Integer getCountUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

    private Integer getCountSessions() {
       return  jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sessions", Integer.class);
    }
}