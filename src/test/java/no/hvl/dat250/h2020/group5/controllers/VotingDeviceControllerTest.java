package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteDeviceController.class)
public class VotingDeviceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private VoteService voteService;

  @Test
  public void shouldReturnVotesTest() throws Exception {
    Vote yesVote = new Vote().answer(AnswerType.YES);
    Poll poll = new Poll();
    poll.setId(1L);
    yesVote.setPoll(poll);
    when(voteService.saveVotesFromDevice(any())).thenReturn(Arrays.asList(yesVote));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/votingDevice")
                .content("{\"pollId\": \"1\", \"numberOfYes\":\"1\", \"numberOfNo\":\"0\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":null,\"answer\":\"YES\"}]"));
  }
}
