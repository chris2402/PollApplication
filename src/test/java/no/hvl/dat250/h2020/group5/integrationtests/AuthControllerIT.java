package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIT {

  @Autowired ObjectMapper objectMapper;
  @Autowired TestRestTemplate template;
  @Autowired PasswordEncoder encoder;
  @Autowired UserRepository userRepository;
  @Autowired GuestRepository guestRepository;
  @Autowired DeviceRepository deviceRepository;
  @LocalServerPort private int port;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    deviceRepository.deleteAll();
    guestRepository.deleteAll();

    template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Test
  public void shouldSaveANewUserTest() throws JsonProcessingException {
    String registerUrl = "http://localhost:" + port + "/auth/signup";
    User user = new User().email("username").password("12341234").displayName("hi");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(user), headers);

    ResponseEntity<UserResponse> result =
        template.postForEntity(registerUrl, request, UserResponse.class);

    UserResponse registeredUser = result.getBody();
    Assertions.assertNotNull(registeredUser);
    Assertions.assertNotNull(registeredUser.getId());
    Assertions.assertTrue(registeredUser.getRoles().contains("USER"));
    Assertions.assertFalse(registeredUser.getRoles().contains("ADMIN"));
    Assertions.assertEquals("username", registeredUser.getEmail());
    Assertions.assertEquals(1, userRepository.count());
    Assertions.assertNotNull(userRepository.findById(registeredUser.getId()).get().getPassword());
  }

  @Test
  public void shouldLogInAsUser() throws JsonProcessingException {
    User user =
        new User()
            .displayName("my_display_name")
            .email("testtest")
            .password(encoder.encode("12341234"));
    User savedUser = userRepository.save(user);

    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().email("testtest").password("12341234");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<UserResponse> result =
        template.postForEntity(loginUrl, request, UserResponse.class);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
    Assertions.assertNotNull(result.getBody());
    Assertions.assertTrue(result.getBody().getRoles().contains("USER"));
    Assertions.assertFalse(result.getBody().getRoles().contains("ADMIN"));
    Assertions.assertEquals(savedUser.getId(), result.getBody().getId());
  }

  @Test
  public void shouldLoginAsAdmin() throws JsonProcessingException {
    User user =
        new User()
            .admin(true)
            .displayName("my_display_name")
            .email("mynameisadmin")
            .password(encoder.encode("password"));
    User savedUser = userRepository.save(user);

    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().email("mynameisadmin").password("password");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<UserResponse> result =
        template.postForEntity(loginUrl, request, UserResponse.class);

    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
    Assertions.assertNotNull(result.getBody());
    Assertions.assertTrue(result.getBody().getRoles().contains("ADMIN"));
    Assertions.assertEquals(savedUser.getId(), result.getBody().getId());
  }
}
