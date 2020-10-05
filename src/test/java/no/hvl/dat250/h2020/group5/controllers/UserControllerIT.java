package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
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

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

  @Autowired UserController userController;
  @Autowired UserRepository userRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @LocalServerPort private int port;
  private URL base;
  private User user;

  @BeforeEach
  public void setUp() throws MalformedURLException {
    this.base = new URL("http://localhost:" + port + "/users");
    user = new User().userName("username").password("my password");
    testRestTemplate
        .getRestTemplate()
        .setRequestFactory(
            new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to make PATCH
    // request
  }

  @AfterEach
  public void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  public void shouldSaveANewUserTest() {
    ResponseEntity<UserResponse> result =
        testRestTemplate.postForEntity(base.toString(), user, UserResponse.class);
    UserResponse postedUser = result.getBody();
    Assertions.assertNotNull(postedUser.getId());
    Assertions.assertEquals(user.getUsername(), postedUser.getUsername());
    Assertions.assertEquals(1, userRepository.count());
    Assertions.assertNotNull(userRepository.findById(postedUser.getId()).get().getPassword());
    Assertions.assertEquals(
        "my password", userRepository.findById(postedUser.getId()).get().getPassword());
  }

  @Test
  public void shouldUpdatePasswordTest() {
    UpdateUserRequest newPasswordRequest = new UpdateUserRequest();
    newPasswordRequest.setOldPassword("my password");
    newPasswordRequest.setNewPassword("my new password");

    ResponseEntity<UserResponse> newUserResult =
        testRestTemplate.postForEntity(base.toString(), user, UserResponse.class);
    UserResponse newUser = newUserResult.getBody();
    Long userId = newUser.getId();

    String pathToEndpoint = base.toString() + "/" + userId.toString();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newPasswordRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertEquals("my new password", userRepository.findById(userId).get().getPassword());
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  public void shouldUpdateUsernameTest() {
    UpdateUserRequest newUsernameRequest = new UpdateUserRequest();
    newUsernameRequest.setUsername("my new username");

    ResponseEntity<UserResponse> newUserResult =
        testRestTemplate.postForEntity(base.toString(), user, UserResponse.class);
    UserResponse newUser = newUserResult.getBody();
    Long userId = newUser.getId();

    String pathToEndpoint = base.toString() + "/" + userId.toString();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newUsernameRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertEquals("my new username", userRepository.findById(userId).get().getUsername());
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  public void shouldDeleteUserTest() {
    ResponseEntity<UserResponse> newUserResult =
        testRestTemplate.postForEntity(base.toString(), user, UserResponse.class);
    UserResponse newUser = newUserResult.getBody();
    Long userId = newUser.getId();

    String pathToEndpoint = base.toString() + "/" + userId.toString();

    testRestTemplate.delete(pathToEndpoint);

    Assertions.assertEquals(0, userRepository.count());
  }
}
