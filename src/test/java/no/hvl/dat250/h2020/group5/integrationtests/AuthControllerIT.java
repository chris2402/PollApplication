package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIT {

  @Autowired ObjectMapper objectMapper;
  @Autowired TestRestTemplate template;
  @Autowired PasswordEncoder encoder;
  @Autowired UserRepository userRepository;
  @Autowired GuestRepository guestRepository;
  @LocalServerPort private int port;

  private User savedUser;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    User user = new User().userName("testtest").password(encoder.encode("12341234"));
    savedUser = userRepository.save(user);
  }

  @Test
  public void shouldSaveANewUserTest() throws JsonProcessingException {
    String registerUrl = "http://localhost:" + port + "/auth/signup";
    User registerRequest = new User().userName("username").password("12341234");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(registerRequest), headers);

    ResponseEntity<UserResponse> result =
        template.postForEntity(registerUrl, request, UserResponse.class);

    UserResponse postedUser = result.getBody();
    Assertions.assertNotNull(postedUser.getId());
    Assertions.assertEquals("username", postedUser.getUsername());
    Assertions.assertEquals(2, userRepository.count());
    Assertions.assertNotNull(userRepository.findById(postedUser.getId()).get().getPassword());
  }

  @Test
  public void shouldLogIn() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().username("testtest").password("12341234");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<String> result = template.postForEntity(loginUrl, request, String.class);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
  }

  @Test
  public void shouldLogInGuest() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signup/guest";
    Guest loginRequest = new Guest().username("guest");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<String> result = template.postForEntity(loginUrl, request, String.class);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
  }
}
