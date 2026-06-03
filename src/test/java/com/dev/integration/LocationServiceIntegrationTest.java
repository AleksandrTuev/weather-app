package com.dev.integration;

import com.dev.config.*;
import com.dev.dto.OpenWeatherCityDto;
import com.dev.dto.OpenWeatherGeoDto;
import com.dev.dto.UserSignUpDto;
import com.dev.exception.UserNotFoundException;
import com.dev.model.Location;
import com.dev.model.User;
import com.dev.repository.LocationRepository;
import com.dev.repository.UserRepository;
import com.dev.service.LocationService;
import com.dev.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TestDataConfig.class, TestSpringConfig.class, TestLiquibaseConfig.class })
@Transactional
public class LocationServiceIntegrationTest {
    @Autowired
    private LocationService locationService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${api.key}")
    private String apiKey;
    @Value("${url.city}")
    private String urlCity;
    @Value("${url.geo}")
    private String urlGeo;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldGetListLocations() {
        String desiredLocation = "Казань";

        mockServer.expect(ExpectedCount.once(), requestTo(UriComponentsBuilder
                                        .fromUriString(urlGeo)
                                        .queryParam("q", desiredLocation)
                                        .queryParam("units", "metric")
                                        .queryParam("limit", 5)
                                        .queryParam("APPID", apiKey)
                                        .encode()
                                        .build()
                                        .toUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getJsonGeoKazan())
                );

        List<OpenWeatherGeoDto> list = locationService.getSearchedLocations(desiredLocation);

        mockServer.verify();
        Assertions.assertNotNull(list);

        boolean hasKazan = list.stream().anyMatch(dto -> dto.getName().equals("Kazan"));
        Assertions.assertTrue(hasKazan);
    }

    @Test
    void shouldGetSaveListLocations() throws URISyntaxException {
        String login = "admin";
        String password = "0123456789";

        userService.signUp(new UserSignUpDto(login, password, password));
        User user = userRepository.findByName(login).orElseThrow(
                () -> new UserNotFoundException("User was not saved")
        );
        int userId = user.getId();

        Location location = Location.builder()
                .id(1)
                .name("Казань")
                .userId(userId)
                .latitude(BigDecimal.valueOf(55.7823547))
                .longitude(BigDecimal.valueOf(49.1242266))
                .build();

        Location location1 = Location.builder()
                .id(2)
                .name("Москва")
                .userId(userId)
                .latitude(BigDecimal.valueOf(55.7504461))
                .longitude(BigDecimal.valueOf(37.6174943))
                .build();

        locationRepository.create(location);
        locationRepository.create(location1);

        OpenWeatherCityDto locationDto = mapper.convertValue(location, OpenWeatherCityDto.class);
        OpenWeatherCityDto locationDto1 = mapper.convertValue(location1, OpenWeatherCityDto.class);

        mockServer.expect(ExpectedCount.once(), requestTo(UriComponentsBuilder
                        .fromUriString(urlCity)
                        .queryParam("lat", String.valueOf(locationDto.getLatitude()))
                        .queryParam("lon", String.valueOf(locationDto.getLongitude()))
                        .queryParam("units", "metric")
                        .queryParam("APPID", apiKey)
                        .encode()
                        .build()
                        .toUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getJsonCityKazan())
                );

        mockServer.expect(ExpectedCount.once(), requestTo(UriComponentsBuilder
                        .fromUriString(urlCity)
                        .queryParam("lat", String.valueOf(locationDto1.getLatitude()))
                        .queryParam("lon", String.valueOf(locationDto1.getLongitude()))
                        .queryParam("units", "metric")
                        .queryParam("APPID", apiKey)
                        .encode()
                        .build()
                        .toUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getJsonCityMoscow())
                );

        List<OpenWeatherCityDto> list = locationService.getSaveLocations(userId);

        mockServer.verify();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(2, list.size());

        boolean hasMoscow = list.stream().anyMatch(dto -> "Москва".equals(dto.getNameLocation()));
        boolean hasKazan = list.stream().anyMatch(dto -> "Казань".equals(dto.getNameLocation()));

        Assertions.assertTrue(hasMoscow);
        Assertions.assertTrue(hasKazan);
    }

    @Test
    void shouldThrowExceptionWhenOpenWeatherReturn401() {
        String location = "Moscow";
        mockServer.expect(ExpectedCount.once(), requestTo(UriComponentsBuilder
                        .fromUriString(urlGeo)
                        .queryParam("q", location)
                        .queryParam("units", "metric")
                        .queryParam("limit", 5)
                        .queryParam("APPID", apiKey)
                        .encode()
                        .build()
                        .toUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"cod\":401, \"message\": \"" +
                              "Invalid API key. Please see https://openweathermap.org/faq#error401 for more info.\"}")
                );
        Assertions.assertThrows(Exception.class, () -> locationService.getSearchedLocations(location));
    }

    private String getJsonGeoKazan() {
        return "[{\"name\":\"Kazan\",\"local_names\":{\"nl\":\"Kazan\",\"de\":\"Kasan\",\"" +
               "ru\":\"городской округ Казань\",\"az\":\"Kazan\",\"he\":\"קאזאן\",\"hi\":\"काज़ान\",\"" +
               "uz\":\"Qozon\",\"ba\":\"Ҡазан\",\"fr\":\"Kazan\",\"uk\":\"Казань\",\"sk\":\"Kazaň\",\"" +
               "zh\":\"喀山\",\"da\":\"Kazan\",\"ro\":\"Kazan\",\"hr\":\"Kazanj\",\"ar\":\"قازان\",\"" +
               "os\":\"Хъазан\",\"pl\":\"Kazań\",\"tk\":\"Kazan\",\"cs\":\"Kazaň\",\"cv\":\"Хусан\"" +
               ",\"hy\":\"Կազան\",\"et\":\"Kaasan\",\"ka\":\"ყაზანი\",\"eo\":\"Kazano\",\"es\":\"Kazán\"" +
               ",\"tr\":\"Kazan\",\"sr\":\"Казањ\",\"lt\":\"Kazanės miesto apygarda\",\"kn\":\"ಕಾಜಾ಼ನ್\"" +
               ",\"lv\":\"Kazaņa\",\"en\":\"Kazan\",\"ko\":\"카잔\",\"id\":\"Kazan\",\"it\":\"Kazan'\"" +
               ",\"fi\":\"Kazan\",\"ascii\":\"Kazan\",\"feature_name\":\"Kazan\",\"pt\":\"Cazã\",\"ca\"" +
               ":\"districte urbà de Kazan\",\"tt\":\"Казан шәһәр бүлгесе\",\"ja\":\"カザン\",\"kv\":\"" +
               "Казан\",\"oc\":\"Kazan\",\"kk\":\"Қазан\",\"hu\":\"Kazany\"},\"lat\":55.7823547,\"lon\"" +
               ":49.1242266,\"country\":\"RU\",\"state\":\"Tatarstan\"},{\"name\":\"Kazan\",\"" +
               "local_names\":{\"et\":\"Kaasan\",\"de\":\"Kasan\",\"uk\":\"Казань\",\"uz\":\"Qozon\"" +
               ",\"oc\":\"Kazan\",\"sk\":\"Kazaň\",\"nl\":\"Kazan\",\"kv\":\"Казан\",\"ca\":\"Kazan\"" +
               ",\"ko\":\"카잔\",\"sr\":\"Казањ\",\"cs\":\"Kazaň\",\"ja\":\"カザン\",\"he\":\"קאזאן\",\"" +
               "en\":\"Kazan\",\"az\":\"Kazan\",\"pt\":\"Cazã\",\"tk\":\"Kazan\",\"feature_name\":\"" +
               "Kazan\",\"id\":\"Kazan\",\"hr\":\"Kazanj\",\"ru\":\"Казань\",\"ro\":\"Kazan\",\"lt\":\"" +
               "Kazanė\",\"hy\":\"Կազան\",\"es\":\"Kazán\",\"kn\":\"ಕಾಜಾ಼ನ್\",\"tr\":\"Kazan\",\"eo\":\"" +
               "Kazano\",\"tt\":\"Казан\",\"da\":\"Kazan\",\"ka\":\"ყაზანი\",\"ba\":\"Ҡазан\",\"lv\":\"" +
               "Kazaņa\",\"kk\":\"Қазан\",\"hi\":\"काज़ान\",\"pl\":\"Kazań\",\"hu\":\"Kazany\",\"it\":\"" +
               "Kazan'\",\"fi\":\"Kazan\",\"os\":\"Хъазан\",\"zh\":\"喀山\",\"ascii\":\"Kazan\",\"cv\":\"" +
               "Хусан\",\"ku\":\"Kazan\",\"ar\":\"قازان\",\"fr\":\"Kazan\"},\"lat\":55.7823547,\"lon\"" +
               ":49.1242266,\"country\":\"RU\",\"state\":\"Tatarstan\"},{\"name\":\"Kahramankazan\",\"" +
               "local_names\":{\"tr\":\"Kahramankazan\",\"ur\":\"کازان\",\"zh\":\"卡拉曼卡贊\",\"ce\":\"" +
               "КахӀраманказан\",\"fa\":\"کازان\",\"ja\":\"カフラマンカザン\"},\"lat\":40.2054445,\"lon\"" +
               ":32.6813148,\"country\":\"TR\"},{\"name\":\"Dərəli\",\"local_names\":{\"hy\":\"Քազան\"" +
               ",\"az\":\"Dərəli\"},\"lat\":39.1802151,\"lon\":46.4433082,\"country\":\"AZ\",\"state\"" +
               ":\"East Zangezur\"},{\"name\":\"Qazançı\",\"local_names\":{\"az\":\"Qazançı\",\"hy\":\"" +
               "Քազան\"},\"lat\":39.1853952,\"lon\":46.4366107,\"country\":\"AZ\",\"state\":\"East Zangezur\"}]";
    }

    private String getJsonCityKazan() {
        return "{\"coord\":{\"lon\":49.1242,\"lat\":55.7824},\"weather\":[{\"id\":501,\"" +
               "main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"base\":\"" +
               "stations\",\"main\":{\"temp\":10.4,\"feels_like\":9.95,\"temp_min\":10.4,\"" +
               "temp_max\":10.4,\"pressure\":1004,\"humidity\":94,\"sea_level\":1004,\"" +
               "grnd_level\":991},\"visibility\":8901,\"wind\":{\"speed\":5.81,\"deg\":250,\"" +
               "gust\":10.71},\"rain\":{\"1h\":1.72},\"clouds\":{\"all\":100},\"dt\":1780055404,\"" +
               "sys\":{\"type\":1,\"id\":9038,\"country\":\"RU\",\"sunrise\":1780013450,\"sunset\"" +
               ":1780074675},\"timezone\":10800,\"id\":551487,\"name\":\"Kazan’\",\"cod\":200}";
    }

    private String getJsonCityMoscow() {
        return "{\"coord\":{\"lon\":37.6175,\"lat\":55.7504},\"weather\":[{\"id\":804,\"main\"" +
               ":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"base\":\"" +
               "stations\",\"main\":{\"temp\":8.02,\"feels_like\":6.08,\"temp_min\":7.34,\"temp_max\"" +
               ":8.29,\"pressure\":1006,\"humidity\":81,\"sea_level\":1006,\"grnd_level\":987},\"" +
               "visibility\":10000,\"wind\":{\"speed\":3.08,\"deg\":331,\"gust\":3.17},\"clouds\"" +
               ":{\"all\":96},\"dt\":1780055467,\"sys\":{\"type\":2,\"id\":2109950,\"country\":\"" +
               "RU\",\"sunrise\":1780016222,\"sunset\":1780077427},\"timezone\":10800,\"id\"" +
               ":524901,\"name\":\"Moscow\",\"cod\":200}";
    }
}
