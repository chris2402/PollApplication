package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
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

import java.net.MalformedURLException;
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
  @Autowired LoginUserInTest loginUserInTest;
  @Autowired PasswordEncoder encoder;
  @Autowired ObjectMapper objectMapper;

  @LocalServerPort private int port;
  private URL base;
  private Guest guest;
  private List<Guest> guests = new ArrayList<>();

  @BeforeEach
  public void setUp() throws MalformedURLException {
    User user = new User().email("testtest").password(encoder.encode("password")).admin(true);
    userRepository.save(user);

    guest = new Guest().displayName("Guest 127348");
    Guest guest2 = new Guest().displayName("Guest 30");
    Guest guest3 = new Guest().displayName("Guest 60");

    guests.addAll(Arrays.asList(guest, guest2, guest3));
    guestRepository.save(guest);
    guestRepository.save(guest2);
    guestRepository.save(guest3);

    this.base = new URL("http://localhost:" + port + "/guests");
    template
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to
    // make PATCH request
  }

  @AfterEach
  public void tearDown() {
    guestRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void shouldRegisterGuestAndPlaceCookie() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/guests/signup";
    Guest loginRequest = new Guest().displayName("guest");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<GuestResponse> result =
        template.postForEntity(loginUrl, request, GuestResponse.class);
    System.out.println(result);
    Assertions.assertTrue(result.getHeaders().containsKey("Set-Cookie"));
    Assertions.assertNotNull(result.getHeaders().get("Set-Cookie"));
    Assertions.assertEquals("guest", result.getBody().getDisplayName());
  }

  @Test
  public void shouldGetAllGuestsAsAdmin() throws JsonProcessingException {
    loginUserInTest.login("testtest", "password", "/auth/signin", port, template, objectMapper);

    ResponseEntity<GuestResponse[]> response =
        template.getForEntity(base.toString(), GuestResponse[].class);
    GuestResponse[] guestsResponses = response.getBody();

    Assertions.assertNotNull(guests);
    Assertions.assertNotNull(guestsResponses);
    Assertions.assertEquals(3, guestsResponses.length);

    int i = 0;
    for (GuestResponse guestResponse : guestsResponses) {
      Assertions.assertEquals(guestResponse.getDisplayName(), guests.get(i).getDisplayName());
      i++;
    }
  }
}
