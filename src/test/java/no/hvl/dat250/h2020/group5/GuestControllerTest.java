package no.hvl.dat250.h2020.group5;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.GuestController;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestController.class)
public class GuestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GuestService guestService;

  private Guest guest1;
  private Guest guest2;

  static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setUp() {
    guest1 = new Guest();
    guest1.setId(1L);
    guest1.setUsername("guest 123");

    guest2 = new Guest();
    guest2.setId(2L);
    guest2.setUsername("guest 345");
  }

  @Test
  public void shouldReturnOneGuestTest() throws Exception {
    when(guestService.getAllGuests())
        .thenReturn(Arrays.asList(new GuestResponse(guest1), new GuestResponse(guest2)));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/guests").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].username", is("guest 123")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].username", is("guest 345")));
  }

  @Test
  public void shouldCreateGuestTest() throws Exception {
    when(guestService.createGuest(any(Guest.class))).thenReturn(new GuestResponse(guest1));

    Guest newGuest = new Guest();
    newGuest.setUsername("guest 123");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/guests")
                .content(asJsonString(newGuest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.username", is("guest 123")));
  }
}
