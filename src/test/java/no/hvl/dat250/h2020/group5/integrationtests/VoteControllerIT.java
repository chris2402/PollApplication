package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.VoteController;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoteControllerIT {

  @Autowired VoteController voteController;
  @Autowired VoteRepository voteRepository;
  @Autowired GuestRepository guestRepository;
  @Autowired UserRepository userRepository;
  @Autowired PollRepository pollRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired ObjectMapper objectMapper;
  @Autowired PasswordEncoder encoder;
  @LocalServerPort private int port;
  private URL base;

  private Poll savedPoll;
  private User savedUser;
  private Guest savedGuest;

  @BeforeEach
  public void setUp() throws MalformedURLException, JsonProcessingException {
    for (Vote vote : voteRepository.findAll()) {
      vote.setVoterOnlyOnVoteSide(null);
      vote.setPollOnlyOnVoteSide(null);
      voteRepository.save(vote);
    }

    voteRepository.deleteAll();
    pollRepository.deleteAll();
    userRepository.deleteAll();
    guestRepository.deleteAll();

    this.base = new URL("http://localhost:" + port + "/votes");

    Poll poll =
        new Poll()
            .visibilityType(PollVisibilityType.PUBLIC)
            .pollDuration(1000)
            .name("my first poll")
            .startTime(Date.from(Instant.now()));
    this.savedPoll = pollRepository.save(poll);
    User user = new User().userName("username").password(encoder.encode("password"));
    this.savedUser = userRepository.save(user);

    testRestTemplate
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @AfterEach
  public void tearDown() {
    for (Vote vote : voteRepository.findAll()) {
      vote.setVoterOnlyOnVoteSide(null);
      vote.setPollOnlyOnVoteSide(null);
      voteRepository.save(vote);
    }

    voteRepository.deleteAll();
    pollRepository.deleteAll();
    userRepository.deleteAll();
    guestRepository.deleteAll();
  }

  private void login(String username, String password) throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signin";
    LoginRequest loginRequest = new LoginRequest().username(username).password(password);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<String> response =
        testRestTemplate.postForEntity(loginUrl, request, String.class);
  }

  private Long registerAsGuest() throws JsonProcessingException {
    String loginUrl = "http://localhost:" + port + "/auth/signup/guest";
    Guest loginRequest = new Guest().username("guest");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request =
        new HttpEntity<>(objectMapper.writeValueAsString(loginRequest), headers);

    ResponseEntity<GuestResponse> guestResponse =
        testRestTemplate.postForEntity(loginUrl, request, GuestResponse.class);

    return guestResponse.getBody().getId();
  }

  @Test
  public void shouldVoteByUserTest() throws JsonProcessingException {
    login("username", "password");

    CastVoteRequest voteRequest = new CastVoteRequest();
    voteRequest.setVote("YES");

    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(
            base.toString() + "/" + savedPoll.getId(), voteRequest, Vote.class);

    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote);
    Assertions.assertEquals(AnswerType.YES, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(savedUser.getId(), voteRepository.findAll().get(0).getVoter().getId());
  }

  @Test
  public void shouldVoteByGuestTest() throws JsonProcessingException {
    Long guestId = registerAsGuest();

    CastVoteRequest voteRequest = new CastVoteRequest();
    voteRequest.setVote("NO");

    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(
            base.toString() + "/" + savedPoll.getId(), voteRequest, Vote.class);
    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote.getId());
    Assertions.assertEquals(AnswerType.NO, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(guestId, voteRepository.findAll().get(0).getVoter().getId());
  }
}
