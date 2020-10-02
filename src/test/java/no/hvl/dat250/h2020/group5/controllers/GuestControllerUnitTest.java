package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestController.class)
public class GuestControllerUnitTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @MockBean private GuestService guestService;

  private Guest guest1;
  private Guest guest2;

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
    List<GuestResponse> resultList =
        Arrays.asList(new GuestResponse(guest1), new GuestResponse(guest2));
    when(guestService.getAllGuests()).thenReturn(resultList);

    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/guests").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(actualResponseBody, objectMapper.writeValueAsString(resultList));
  }

  @Test
  public void shouldCreateGuestTest() throws Exception {
    GuestResponse guestResponse = new GuestResponse(guest1);
    when(guestService.createGuest(any(Guest.class))).thenReturn(guestResponse);

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/guests")
                    .content(objectMapper.writeValueAsString(guest1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(actualResponseBody, objectMapper.writeValueAsString(guestResponse));
  }
}
