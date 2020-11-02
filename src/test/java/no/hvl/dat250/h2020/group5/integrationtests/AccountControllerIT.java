package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.AccountRepository;
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
public class AccountControllerIT {

  @Autowired AccountRepository accountRepository;
  @Autowired UserRepository userRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired ObjectMapper objectMapper;
  @Autowired PasswordEncoder encoder;
  @Autowired LoginUserInTest loginUserInTest;
  @LocalServerPort private int port;
  private URL base;
  private Account savedAccount;

  @BeforeEach
  public void setUp() throws MalformedURLException, JsonProcessingException {
    accountRepository.deleteAll();
    this.base = new URL("http://localhost:" + port + "/users");

    User user = new User().displayName("my_name");
    Account account = new Account().email("email").password(encoder.encode("my password"));
    account.setUserAndAddThisToUser(user);

    User user2 = new User().displayName("my_name");
    Account adminAccount =
        new Account().email("admin").password(encoder.encode("my password")).admin(true);
    adminAccount.setUserAndAddThisToUser(user2);

    savedAccount = accountRepository.save(account);
    accountRepository.save(adminAccount);

    testRestTemplate
        .getRestTemplate()
        .setRequestFactory(
            new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to make PATCH

    loginUserInTest.login(
        "email", "my password", "/auth/signin", port, testRestTemplate, objectMapper);
  }

  @AfterEach
  public void tearDown() {
    accountRepository.deleteAll();
  }

  @Test
  public void shouldGetInfoAboutCurrentlyLoggedInAccount() {
    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(base.toString() + "/me", UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedAccount.getId(), userResponse.getId());
  }

  @Test
  public void shouldGetAccountsAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "admin", "my password", "/auth/signin", port, testRestTemplate, objectMapper);
    ResponseEntity<UserResponse[]> response =
        testRestTemplate.getForEntity(base.toString(), UserResponse[].class);

    System.out.println(response);

    UserResponse[] userResponses = response.getBody();
    Assertions.assertNotNull(userResponses);
    Assertions.assertEquals(2, userResponses.length);
  }

  @Test
  public void shouldGetAccountAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "admin", "my password", "/auth/signin", port, testRestTemplate, objectMapper);

    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(
            base.toString() + "/" + savedAccount.getId(), UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedAccount.getId(), userResponse.getId());
  }

  @Test
  public void shouldGetAccountAsUser() {
    ResponseEntity<UserResponse> response =
        testRestTemplate.getForEntity(
            base.toString() + "/" + savedAccount.getId(), UserResponse.class);

    UserResponse userResponse = response.getBody();
    Assertions.assertNotNull(userResponse);
    Assertions.assertEquals(savedAccount.getId(), userResponse.getId());
  }

  @Test
  public void shouldUpdatePasswordTest() {
    UpdateUserRequest newPasswordRequest = new UpdateUserRequest();
    newPasswordRequest.setOldPassword("my password");
    newPasswordRequest.setNewPassword("my new password");
    System.out.println(savedAccount.getId());
    String pathToEndpoint = base.toString() + "/" + savedAccount.getId();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newPasswordRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertNotNull(accountRepository.findById(savedAccount.getId()).get().getPassword());
    Assertions.assertEquals(2, accountRepository.count());
  }

  @Test
  public void shouldUpdateUsernameTest() {
    UpdateUserRequest newUsernameRequest = new UpdateUserRequest();
    newUsernameRequest.setEmail("my new username");

    String pathToEndpoint = base.toString() + "/" + savedAccount.getId().toString();
    Boolean response =
        testRestTemplate.patchForObject(pathToEndpoint, newUsernameRequest, Boolean.class);

    Assertions.assertTrue(response);
    Assertions.assertEquals(
        "my new username", accountRepository.findById(savedAccount.getId()).get().getEmail());
    Assertions.assertEquals(2, accountRepository.count());
  }

  @Test
  public void shouldDeleteAccountTest() {
    String pathToEndpoint = base.toString() + "/" + savedAccount.getId().toString();
    testRestTemplate.delete(pathToEndpoint);

    Assertions.assertEquals(1, accountRepository.count());
    Assertions.assertEquals(1, userRepository.count());
    Assertions.assertTrue(accountRepository.findById(savedAccount.getId()).isEmpty());
  }
}
