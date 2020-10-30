package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.VoterController;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
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
public class VoterControllerIT {

  @Autowired VoterController userController;
  @Autowired VoterRepository voterRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired ObjectMapper objectMapper;
  @Autowired PasswordEncoder encoder;
  @Autowired LoginUserInTest loginUserInTest;
  @LocalServerPort private int port;
  private URL base;
  private User savedUser;

  @BeforeEach
  public void setUp() throws MalformedURLException, JsonProcessingException {
    voterRepository.deleteAll();
    this.base = new URL("http://localhost:" + port + "/voters");

    User user = new User().userName("username").password(encoder.encode("my password"));
    User admin = new User().userName("admin").password(encoder.encode("my password")).admin(true);

    savedUser = voterRepository.save(user);
    voterRepository.save(admin);

    testRestTemplate
        .getRestTemplate()
        .setRequestFactory(
            new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to make PATCH

    loginUserInTest.login(
        "username", "my password", "/auth/signin", port, testRestTemplate, objectMapper);
  }

  @AfterEach
  public void tearDown() {
    voterRepository.deleteAll();
  }

  @Test
  public void shouldGetInfoAboutCurrentlyLoggedInUser() {
    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(base.toString() + "/me", UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedUser.getId(), userResponse.getId());
  }
}
