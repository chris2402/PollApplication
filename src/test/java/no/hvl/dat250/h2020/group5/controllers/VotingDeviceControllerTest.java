package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.service.VoteService;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {VotingDevice.class})
@WebMvcTest(VotingDevice.class)
@WithMockUser
public class VotingDeviceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private VoteService voteService;
  private Vote yesVote;
  private Vote noVote;
  private Poll poll;

  @BeforeEach
  public void setUp() {
    this.yesVote = new Vote().answer(AnswerType.YES);
    this.noVote = new Vote().answer(AnswerType.NO);
    this.poll = new Poll();

    yesVote.setPollAndAddThisVoteToPoll(poll);
    noVote.setPollAndAddThisVoteToPoll(poll);
  }

  @Test
  public void shouldSaveOneYesVoteTest() throws Exception {
    when(voteService.saveVotesFromDevice(any())).thenReturn(Collections.singletonList(yesVote));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/votingDevice")
                .content(
                    "{\"pollId\": \"1\", \"deviceId\": \"1\", \"numberOfYes\":\"1\", \"numberOfNo\":\"0\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":null,\"answer\":\"YES\"}]"));
  }

  @Test
  public void shouldSaveTwoYesAndFourVotesTest() throws Exception {
    when(voteService.saveVotesFromDevice(any()))
        .thenReturn(Arrays.asList(yesVote, yesVote, noVote, noVote, noVote, noVote));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/votingDevice")
                .content(
                    "{\"pollId\": \"1\", \"deviceId\": \"1\", \"numberOfYes\":\"2\", \"numberOfNo\":\"4\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    "[{\"id\":null,\"answer\":\"YES\"}, {\"id\":null,\"answer\":\"YES\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}]"));
  }
}
