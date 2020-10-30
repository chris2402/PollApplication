package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.responses.VoterResponse;
import no.hvl.dat250.h2020.group5.service.VoterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {VoterController.class, ExtractIdFromAuth.class})
@WebMvcTest(VoterController.class)
@WithMockUser
public class VoterControllerTest {

  VoterResponse voterResponse;
  @Autowired private MockMvc mockMvc;
  @MockBean private ExtractIdFromAuth extractIdFromAuth;
  @MockBean private VoterService voterService;

  @BeforeEach
  public void setUp() {
    Voter voter = new User().userName("my_awesome_name");
    voter.setId(1L);

    this.voterResponse = new VoterResponse(voter);
    when(voterService.getVoter(eq(1L))).thenReturn(voterResponse);
  }

  @Test
  public void shouldReturnCurrentlyLoggedInUserTest() throws Exception {
    when(extractIdFromAuth.getIdFromAuth(any(Authentication.class))).thenReturn(1L);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/voters/me").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    "{\"roles\":[\"ROLE_USER\"],\"id\":1,\"username\":\"my_awesome_name\",\"displayName\":null}"));
  }
}
