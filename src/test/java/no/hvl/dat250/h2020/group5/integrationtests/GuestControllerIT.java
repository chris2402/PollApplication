package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import org.junit.jupiter.api.AfterEach;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GuestControllerIT {

  @Autowired TestRestTemplate template;
  @Autowired GuestRepository guestRepository;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder encoder;
  @Autowired ObjectMapper objectMapper;

  @LocalServerPort private int port;
  private URL base;
  private Guest guest;
  private List<Guest> guests = new ArrayList<>();

  @BeforeEach
  public void setUp() throws Exception {
    User user = new User().userName("testtest").password(encoder.encode("password")).admin(true);
    userRepository.save(user);

    this.guest = new Guest();
    this.guest.setUsername("Guest 127348");
    this.guest.setId(20L);

    Guest guest2 = new Guest();
    guest2.setUsername("Guest 30");
    guest2.setId(30L);

    Guest guest3 = new Guest();
    guest3.setUsername("Guest 60");
    guest3.setId(60L);

    guests.addAll(Arrays.asList(guest, guest2, guest3));

    guestRepository.save(guest);
    guestRepository.save(guest2);
    guestRepository.save(guest3);

    this.base = new URL("http://localhost:" + port + "/guests");
    template
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to
    // make PATCH request
    login();
  }

  private void login() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().username("testtest").password("password");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    template.postForEntity(loginUrl, request, String.class);
  }

  @AfterEach
  public void tearDown() {
    guestRepository.deleteAll();
  }

  @Test
  public void shouldGetAllGuestsAsAdmin() {
    ResponseEntity<GuestResponse[]> response =
        template.getForEntity(base.toString(), GuestResponse[].class);
    GuestResponse[] guestsResponses = response.getBody();

    System.out.println(guestsResponses.toString());
    Assertions.assertNotNull(guests);
    Assertions.assertEquals(3, guestsResponses.length);

    int i = 0;
    for (GuestResponse guestResponse : guestsResponses) {
      Assertions.assertEquals(guestResponse.getUsername(), guests.get(i).getUsername());
      i++;
    }
  }
}
