package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.VoteController;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.*;
import no.hvl.dat250.h2020.group5.requests.VoteRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
  @Autowired AccountRepository accountRepository;
  @Autowired GuestRepository guestRepository;
  @Autowired UserRepository userRepository;
  @Autowired PollRepository pollRepository;
  @Autowired TestRestTemplate testRestTemplate;
  @Autowired LoginUserInTest loginUserInTest;
  @Autowired ObjectMapper objectMapper;
  @Autowired PasswordEncoder encoder;
  @LocalServerPort private int port;
  private URL base;

  private Poll savedPoll;
  private Account savedAccount;
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
    accountRepository.deleteAll();

    this.base = new URL("http://localhost:" + port + "/votes");

    Poll poll =
        new Poll()
            .visibilityType(PollVisibilityType.PUBLIC)
            .pollDuration(1000)
            .name("my first poll")
            .startTime(Date.from(Instant.now()));
    this.savedPoll = pollRepository.save(poll);

    User user = new User();
    Account account = new Account().email("username").password(encoder.encode("password"));
    account.setUserAndAddThisToUser(user);
    this.savedAccount = accountRepository.save(account);

    Guest guest = new Guest().displayName("guest");
    this.savedGuest = guestRepository.save(guest);

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
    accountRepository.deleteAll();
  }

  @Test
  public void shouldVoteByUserTest() throws JsonProcessingException {
    loginUserInTest.login(
        "username", "password", "/auth/signin", port, testRestTemplate, objectMapper);

    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setVote("YES");

    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(
            base.toString() + "/" + savedPoll.getId(), voteRequest, Vote.class);

    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote);
    Assertions.assertEquals(AnswerType.YES, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(
        savedAccount.getUser().getId(), voteRepository.findAll().get(0).getVoter().getId());
  }

  @Test
  public void shouldVoteAsGuestTest() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setVote("NO");
    voteRequest.setId(savedGuest.getId());

    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(
            base.toString() + "/" + savedPoll.getId(), voteRequest, Vote.class);
    System.out.println(response);
    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote);
    Assertions.assertNotNull(savedVote.getId());
    Assertions.assertEquals(AnswerType.NO, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(savedGuest.getId(), voteRepository.findAll().get(0).getVoter().getId());
  }

  @Test
  public void shouldFailToVoteAsGuestWithoutIdTest() {
    VoteRequest voteRequest = new VoteRequest();
    voteRequest.setVote("NO");

    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(
            base.toString() + "/" + savedPoll.getId(), voteRequest, Vote.class);
    System.out.println(response);
    Vote savedVote = response.getBody();
    Assertions.assertNull(savedVote);
  }
}
