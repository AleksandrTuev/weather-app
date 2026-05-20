import com.dev.config.LiquibaseConfig;
import com.dev.config.SpringConfig;
import com.dev.dto.OpenWeatherCityDto;
import com.dev.dto.OpenWeatherGeoDto;
import com.dev.dto.UserSignUpDto;
import com.dev.model.Location;
import com.dev.repository.LocationRepository;
import com.dev.service.LocationService;
import com.dev.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringConfig.class, LiquibaseConfig.class })
@WebAppConfiguration
@Transactional
public class LocationServiceIntegrationTest {
    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserService userService;

    @Test
    void shouldGetListLocations() {
        String desiredLocation = "Казань";
        List<OpenWeatherGeoDto> list = locationService.getSearchedLocations(desiredLocation);
        Assertions.assertNotNull(list);
        Assertions.assertEquals("Kazan", list.get(0).getName());
    }

    @Test
    void shouldGetSaveListLocations() {
        userService.signUp(new UserSignUpDto("admin", "0123456789", "0123456789"));
        MDC.put("userId", "1");

        Location location = Location.builder()
                .id(1)
                .name("Казань")
                .userId(1)
                .latitude(BigDecimal.valueOf(55.7823547))
                .longitude(BigDecimal.valueOf(49.1242266))
                .build();

        Location location1 = Location.builder()
                .id(2)
                .name("Москва")
                .userId(1)
                .latitude(BigDecimal.valueOf(55.7504461))
                .longitude(BigDecimal.valueOf(37.6174943))
                .build();

        locationRepository.create(location);
        locationRepository.create(location1);

        List<OpenWeatherCityDto> list = locationService.getSaveLocations();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals("Moscow", list.get(0).getNameLocation());
        Assertions.assertEquals("Kazan’", list.get(1).getNameLocation());
    }
}
