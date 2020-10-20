package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.service.VoteService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {VoteController.class})
@WebMvcTest(VoteController.class)
@WithMockUser
public class VoteControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private VoteService voteService;
  @MockBean private ExtractIdFromAuth extractIdFromAuth;

  private Vote vote;

  @BeforeEach
  public void setUp() {
    this.vote = new Vote();
    vote.setId(1L);
    vote.setAnswer(AnswerType.YES);
  }

  @Test
  public void shouldCreateNewVoteTest() throws Exception {
    when(voteService.vote(eq(1L), eq(1L), any(CastVoteRequest.class))).thenReturn(vote);
    when(extractIdFromAuth.getIdFromAuth(any(Authentication.class))).thenReturn(1L);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/votes/1")
                .content("{\"vote\":\"YES\"}")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"id\":1, \"answer\":\"YES\"}"));
  }

  @Test
  public void shouldGetVoteTest() throws Exception {
    when(voteService.findVote(anyLong(), anyLong())).thenReturn(vote);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/votes?userId=1&pollId=1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"id\":1, \"answer\":\"YES\"}"));
  }
}
