package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
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

import java.util.UUID;

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

  private User savedUser;
  private Guest savedGuest;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    deviceRepository.deleteAll();
    guestRepository.deleteAll();

    User user = new User().userName("testtest").password(encoder.encode("12341234"));
    savedUser = userRepository.save(user);
    String guestName = UUID.randomUUID().toString();
    Guest guest =
        new Guest().username(guestName).password(encoder.encode(guestName)).displayName("guest");
    savedGuest = guestRepository.save(guest);

    template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  private void loginAsAdmin(String username, String password) throws JsonProcessingException {
    User admin =
        new User().admin(true).userName("mynameisadmin").password(encoder.encode("password"));
    userRepository.save(admin);

    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().username(username).password(password);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    template.postForEntity(loginUrl, request, String.class);
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
  public void shouldRegisterAndLogInGuest() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signup/guest";
    Guest loginRequest = new Guest().displayName("guest");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<GuestResponse> result =
        template.postForEntity(loginUrl, request, GuestResponse.class);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
    Assertions.assertEquals("guest", result.getBody().getDisplayName());
  }

  @Test
  public void shouldLogInGuest() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signin/guest";
    Guest loginRequest = new Guest().username(savedGuest.getUsername());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<String> result = template.postForEntity(loginUrl, request, String.class);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
  }

  @Test
  public void shouldSaveANewDeviceTest() throws JsonProcessingException {
    loginAsAdmin("mynameisadmin", "password");

    String registerUrl = "http://localhost:" + port + "/auth/signup/device";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(""), headers);

    ResponseEntity<VotingDeviceResponse> result =
        template.postForEntity(registerUrl, request, VotingDeviceResponse.class);

    VotingDeviceResponse savedDevice = result.getBody();
    Assertions.assertNotNull(savedDevice.getId());
    Assertions.assertEquals(1, deviceRepository.count());
    Assertions.assertNotNull(
        deviceRepository.findVotingDeviceByUsername(savedDevice.getUsername()).get().getPassword());
  }

  @Test
  public void loginADeviceTest() throws JsonProcessingException {
    deviceRepository.save(
        new VotingDevice().username(123456 + "").password(encoder.encode(123456 + "")));

    LoginRequest loginRequest = new LoginRequest().username("123456");

    String registerUrl = "http://localhost:" + port + "/auth/signin/device";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<VotingDeviceResponse> result =
        template.postForEntity(registerUrl, request, VotingDeviceResponse.class);

    VotingDeviceResponse savedDevice = result.getBody();
    Assertions.assertNotNull(savedDevice.getId());
    Assertions.assertEquals(1, deviceRepository.count());
    Assertions.assertNotNull(savedDevice.getJwt());
  }
}
