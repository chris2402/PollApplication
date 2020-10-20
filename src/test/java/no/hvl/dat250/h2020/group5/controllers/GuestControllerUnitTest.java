package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {GuestController.class})
@WebMvcTest(GuestController.class)
@WithMockUser
public class GuestControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GuestService guestService;

  private GuestResponse guestResponse1;
  private GuestResponse guestResponse2;

  @BeforeEach
  public void setUp() {
    Guest guest1 = new Guest().username("guest 123");
    guest1.setId(1L);
    Guest guest2 = new Guest().username("guest 345");
    guest1.setId(2L);

    guestResponse1 = new GuestResponse(guest1);
    guestResponse2 = new GuestResponse(guest2);
  }

  @Test
  public void shouldReturnOneGuestTest() throws Exception {
    List<GuestResponse> resultList = Arrays.asList(guestResponse1, guestResponse2);
    when(guestService.getAllGuests()).thenReturn(resultList);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/guests").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(resultList));
  }
}
