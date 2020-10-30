package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.UserController;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

  @Autowired UserController userController;
  @Autowired UserRepository userRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired ObjectMapper objectMapper;
  @Autowired PasswordEncoder encoder;
  @Autowired LoginUserInTest loginUserInTest;
  @LocalServerPort private int port;
  private URL base;
  private User savedUser;

  @BeforeEach
  public void setUp() throws MalformedURLException, JsonProcessingException {
    userRepository.deleteAll();
    this.base = new URL("http://localhost:" + port + "/users");

    User user = new User().userName("username").password(encoder.encode("my password"));
    User admin = new User().userName("admin").password(encoder.encode("my password")).admin(true);

    savedUser = userRepository.save(user);
    userRepository.save(admin);

    testRestTemplate
        .getRestTemplate()
        .setRequestFactory(
            new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to make PATCH

    loginUserInTest.login(
        "username", "my password", "/auth/signin", port, testRestTemplate, objectMapper);
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  public void shouldGetUsersAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "admin", "my password", "/auth/signin", port, testRestTemplate, objectMapper);
    ResponseEntity<UserResponse[]> response =
        testRestTemplate.getForEntity(base.toString(), UserResponse[].class);

    UserResponse[] userResponses = response.getBody();
    Assertions.assertNotNull(userResponses);
    Assertions.assertEquals(2, userResponses.length);
  }

  @Test
  public void shouldGetUserAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "admin", "my password", "/auth/signin", port, testRestTemplate, objectMapper);

    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(
            base.toString() + "/" + savedUser.getId(), UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedUser.getId(), userResponse.getId());
  }

  @Test
  public void shouldGetUserAsUser() {
    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(
            base.toString() + "/" + savedUser.getId(), UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedUser.getId(), userResponse.getId());
  }

  @Test
  public void shouldUpdatePasswordTest() {
    UpdateUserRequest newPasswordRequest = new UpdateUserRequest();
    newPasswordRequest.setOldPassword("my password");
    newPasswordRequest.setNewPassword("my new password");
    System.out.println(savedUser.getId());
    String pathToEndpoint = base.toString() + "/" + savedUser.getId();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newPasswordRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertNotNull(userRepository.findById(savedUser.getId()).get().getPassword());
    Assertions.assertEquals(2, userRepository.count());
  }

  @Test
  public void shouldUpdateUsernameTest() {
    UpdateUserRequest newUsernameRequest = new UpdateUserRequest();
    newUsernameRequest.setUsername("my new username");

    String pathToEndpoint = base.toString() + "/" + savedUser.getId().toString();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newUsernameRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertEquals(
        "my new username", userRepository.findById(savedUser.getId()).get().getUsername());
    Assertions.assertEquals(2, userRepository.count());
  }

  @Test
  public void shouldDeleteUserTest() {
    String pathToEndpoint = base.toString() + "/" + savedUser.getId().toString();
    testRestTemplate.delete(pathToEndpoint);

    Assertions.assertEquals(1, userRepository.count());
  }
}
