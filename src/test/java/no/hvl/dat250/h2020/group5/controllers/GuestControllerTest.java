package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GuestControllerTest {

  @Autowired TestRestTemplate template;
  @Autowired GuestController guestcontroller;
  @Autowired GuestRepository guestRepository;
  @Autowired VoteRepository voteRepository;

  @LocalServerPort private int port;
  private URL base;
  private Guest guest;
  private List<Guest> guests = new ArrayList<>();

  @BeforeEach
  public void setUp() throws Exception {
    voteRepository.deleteAll();
    guestRepository.deleteAll();

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
  }

  @Test
  public void shouldGetAllGuests() {
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

  @Test
  public void shouldCreateNewGuest() {
    String username = "Guest 40";
    Guest newGuest = new Guest();
    newGuest.setUsername(username);

    ResponseEntity<GuestResponse> response =
        template.postForEntity(base.toString(), newGuest, GuestResponse.class);
    GuestResponse guestResponse = response.getBody();
    Assertions.assertNotNull(guestResponse);
    Optional<Guest> guestFromRepository = guestRepository.findById(guestResponse.getId());

    Assertions.assertNotNull(guestResponse);
    Assertions.assertTrue(guestFromRepository.isPresent());
    Assertions.assertEquals(username, guestFromRepository.get().getUsername());
  }
}
