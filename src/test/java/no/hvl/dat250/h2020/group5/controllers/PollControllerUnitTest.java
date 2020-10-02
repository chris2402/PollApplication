package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PollController.class)
public class PollControllerUnitTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @MockBean private PollService pollService;

  @MockBean private PollRepository pollRepository;

  private PollResponse pollResponse;
  private Poll poll1;
  private Poll poll2;
  private User user;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId(3L);
    user.setUsername("my_awesome_username");

    poll1 = new Poll();
    poll1.setId(1L);
    poll1.setQuestion("my_awesome_question1");
    poll1.setVisibilityType(PollVisibilityType.PRIVATE);
    poll1.setPollOwner(user);

    poll2 = new Poll();
    poll2.setId(2L);
    poll2.setQuestion("my_awesome_question2");
    poll2.setVisibilityType(PollVisibilityType.PUBLIC);
    poll2.setPollOwner(user);
  }

  @Test
  public void shouldReturnAllPublicPollsTest() throws Exception {
    List<PollResponse> resultList = Collections.singletonList(new PollResponse(poll2));
    when(pollService.getAllPublicPolls()).thenReturn(resultList);

    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/polls").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(actualResponseBody, objectMapper.writeValueAsString(resultList));
  }

  @Test
  public void shouldReturnAllPollsOfOwnerTest() throws Exception {
    List<PollResponse> resultList = Arrays.asList(new PollResponse(poll1), new PollResponse(poll2));
    when(pollService.getUserPollsAsOwner(anyLong())).thenReturn(resultList);

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/polls/owner/3").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(actualResponseBody, objectMapper.writeValueAsString(resultList));
  }

  @Test
  public void shouldCreatePoll() throws Exception {
    when(pollService.createPoll(any(Poll.class), anyLong())).thenReturn(new PollResponse(poll1));
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/polls/3")
                    .content(objectMapper.writeValueAsString(poll1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(
        actualResponseBody, objectMapper.writeValueAsString(new PollResponse(poll1)));
  }

  @Test
  public void shouldDeletePollTest() throws Exception {
    when(pollService.deletePoll(anyLong(), anyLong())).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/polls/1/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(true)));
  }
}
