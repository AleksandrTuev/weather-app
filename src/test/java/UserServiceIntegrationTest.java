import com.dev.config.LiquibaseConfig;
import com.dev.config.SpringConfig;
import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.mapper.UserMapper;
import com.dev.model.User;
import com.dev.repository.UserRepository;
import com.dev.service.UserService;
import com.dev.util.ProjectConstants;
import jakarta.servlet.http.Cookie;
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
        User user = new User(null, "bobby", "0123456789");

        UserSignUpDto userSignUpDto = userMapper.toUserSignUpDto(user);

        Cookie cookie = userService.signUp(userSignUpDto);
        assertNotNull(cookie);
        assertEquals(ProjectConstants.SESSION_ID, cookie.getName());
        assertNotNull(cookie.getValue());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertEquals(1, count);

        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sessions", Integer.class);
        assertEquals(1, count);
    }

    @Test
    void testLogIn(){
        User user = new User(null, "tom","0123456789");

        UserSignInDto userSignInDto = userMapper.toUserSignInDto(user);
        user.setPassword(passwordEncoder.encode("0123456789"));
        userRepository.save(user);

        Cookie cookie = userService.signIn(userSignInDto);
        assertEquals(user.getUsername(), userRepository.findByName(user.getUsername()).get().getUsername());
        assertNotNull(cookie);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sessions", Integer.class);
        assertEquals(1, count);
    }
}