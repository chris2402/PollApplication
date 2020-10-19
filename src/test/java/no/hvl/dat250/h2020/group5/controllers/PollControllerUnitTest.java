package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
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
import java.util.List;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {PollController.class})
@WebMvcTest(PollController.class)
@WithMockUser
public class PollControllerUnitTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @MockBean private PollService pollService;

  private Poll poll1;
  private PollResponse response1;
  private PollResponse response2;
  private User user;

  @BeforeEach
  public void setUp() {
    user = new User().userName("my_awesome_username");
    user.setId(3L);

    poll1 =
        new Poll()
            .question("my_awesome_question1")
            .visibilityType(PollVisibilityType.PRIVATE)
            .pollOwner(user);
    poll1.setId(1L);

    Poll poll2 =
        new Poll()
            .question("my_awesome_question2")
            .visibilityType(PollVisibilityType.PUBLIC)
            .pollOwner(user);

    response1 = new PollResponse(poll1);
    response2 = new PollResponse(poll2);
  }

  @Test
  public void shouldReturnAllPublicPollsTest() throws Exception {
    List<PollResponse> resultList = Collections.singletonList(response2);
    when(pollService.getAllPublicPolls()).thenReturn(resultList);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/polls").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(resultList));
  }

  @Test
  public void shouldReturnAllPollsOfOwnerTest() throws Exception {
    List<PollResponse> resultList = Arrays.asList(response1, response2);
    when(pollService.getUserPollsAsOwner(user.getId())).thenReturn(resultList);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/polls/owner/" + user.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(resultList));
  }

  @Test
  public void shouldReturnAllPollsOfOwnerAsAdminTest() throws Exception {
    List<PollResponse> resultList = Arrays.asList(response1, response2);
    when(pollService.getUserPollsAsAdmin(user.getId(), 4L)).thenReturn(resultList);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/polls/admin/4/polls/" + user.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(resultList));
  }

  @Test
  public void shouldReturnAllPollsAsAdminTest() throws Exception {
    List<PollResponse> resultList = Arrays.asList(response1, response2);
    when(pollService.getAllPolls()).thenReturn(resultList);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/polls/admin/4/polls").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(resultList));
  }

  @Test
  public void shouldCreatePoll() throws Exception {
    when(pollService.createPoll(any(Poll.class), eq(user.getId()))).thenReturn(response1);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/polls/" + user.getId())
                .content(objectMapper.writeValueAsString(poll1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(response1));
  }

  @Test
  public void shouldDeletePollTest() throws Exception {
    when(pollService.deletePoll(poll1.getId(), user.getId())).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/polls/" + poll1.getId() + "/" + user.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(true)));
  }

  @Test
  public void shouldActivatePollTest() throws Exception {
    when(pollService.activatePoll(poll1.getId(), user.getId())).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/polls/" + poll1.getId())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldIndicateIfPollIsActivatedTest() throws Exception {
    when(pollService.isActivated(poll1.getId())).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/polls/" + poll1.getId() + "/active")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldReturnNumberOfVotesTest() throws Exception {
    VotesResponse votesResponse = new VotesResponse().no(0).yes(0);
    when(pollService.getNumberOfVotes(poll1.getId())).thenReturn(votesResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/polls/" + poll1.getId() + "/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(votesResponse));
  }
}
