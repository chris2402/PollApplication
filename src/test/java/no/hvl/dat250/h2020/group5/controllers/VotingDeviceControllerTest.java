package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import no.hvl.dat250.h2020.group5.service.VoteService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {VoteDeviceController.class})
@WebMvcTest(VoteDeviceController.class)
@WithMockUser
public class VotingDeviceControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private VoteService voteService;
  @MockBean private VotingDeviceService votingDeviceService;
  @MockBean private ExtractFromAuth extractFromAuth;

  private Vote yesVote;
  private Vote noVote;
  private Poll poll;
  private VotingDevice votingDevice;
  private VotingDeviceResponse votingDeviceResponse;

  @BeforeEach
  public void setUp() {
    this.yesVote = new Vote().answer(AnswerType.YES);
    this.noVote = new Vote().answer(AnswerType.NO);
    this.poll = new Poll();

    yesVote.setPollAndAddThisVoteToPoll(poll);
    noVote.setPollAndAddThisVoteToPoll(poll);

    votingDevice = new VotingDevice();
    votingDevice.displayName("my-device");
    votingDevice.setId(UUID.randomUUID());

    votingDeviceResponse = new VotingDeviceResponse(votingDevice);
  }

  @Test
  public void shouldSaveOneYesVoteTest() throws Exception {
    when(voteService.saveVotesFromDevice(anyLong(), any(VoteRequestFromDevice.class)))
        .thenReturn(Collections.singletonList(yesVote));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/voting-device/1")
                .content("{\"deviceUUID\": \"1a\", \"numberOfYes\":\"1\", \"numberOfNo\":\"0\"}")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"id\":null,\"answer\":\"YES\"}]"));
  }

  @Test
  public void shouldSaveTwoYesAndFourVotesTest() throws Exception {
    when(voteService.saveVotesFromDevice(anyLong(), any(VoteRequestFromDevice.class)))
        .thenReturn(Arrays.asList(yesVote, yesVote, noVote, noVote, noVote, noVote));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/voting-device/1")
                .content("{\"deviceUUID\": \"1a\", \"numberOfYes\":\"2\", \"numberOfNo\":\"4\"}")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    "[{\"id\":null,\"answer\":\"YES\"}, {\"id\":null,\"answer\":\"YES\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}, {\"id\":null,\"answer\":\"NO\"}]"));
  }

  @Test
  public void shouldCreateAVotingDevice() throws Exception {
    when(extractFromAuth.userId(any(Authentication.class))).thenReturn(UUID.randomUUID());
    when(votingDeviceService.addDeviceToUser(any(UUID.class), any(VotingDevice.class)))
        .thenReturn(votingDeviceResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/voting-device")
                .content(objectMapper.writeValueAsString(votingDevice))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(votingDeviceResponse));
  }

  @Test
  public void shouldDeleteDevice() throws Exception {
    when(extractFromAuth.userId(any(Authentication.class))).thenReturn(UUID.randomUUID());
    when(votingDeviceService.deleteDevice(any(UUID.class), any(UUID.class))).thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/voting-device/" + UUID.randomUUID())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldGetAllUsersDevices() throws Exception {
    when(extractFromAuth.userId(any(Authentication.class))).thenReturn(UUID.randomUUID());
    when(votingDeviceService.getAllDevicesToOwner(any(UUID.class)))
        .thenReturn(Collections.singletonList(votingDeviceResponse));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/voting-device")
                .content(objectMapper.writeValueAsString(votingDevice))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            responseBody().containsObjectAsJson(Collections.singletonList(votingDeviceResponse)));
  }
}
