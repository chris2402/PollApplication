package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

  @Autowired UserController userController;
  @Autowired TestRestTemplate testRestTemplate;
  @LocalServerPort private int port;
  private URL base;

  @BeforeEach
  public void setUp() throws MalformedURLException {
    this.base = new URL("http://localhost:" + port + "/users");
  }

  @Test
  public void shouldSaveANewUserTest() {
    User user = new User().userName("username").password("my password");
    ResponseEntity<UserResponse> result =
        testRestTemplate.postForEntity(base.toString(), user, UserResponse.class);
    UserResponse postedUser = result.getBody();
    Assertions.assertEquals(user.getUsername(), postedUser.getUsername());
    Assertions.assertEquals(user.getPassword(), postedUser.getPassword());
    Assertions.assertNotNull(postedUser.getId());
  }
}
