package no.hvl.dat250.h2020.group5.integrationtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.integrationtests.util.LoginUserInTest;
import no.hvl.dat250.h2020.group5.repositories.*;
import no.hvl.dat250.h2020.group5.requests.CreateOrUpdatePollRequest;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URL;
import java.util.*;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PollControllerIT {

  @Autowired TestRestTemplate template;
  @Autowired AccountRepository accountRepository;
  @Autowired PollRepository pollRepository;
  @Autowired UserRepository userRepository;
  @Autowired GuestRepository guestRepository;
  @Autowired VoteRepository voteRepository;
  @Autowired LoginUserInTest loginUserInTest;
  @Autowired ObjectMapper objectMapper;
  @Autowired ExtractFromAuth extractFromAuth;
  @Autowired PasswordEncoder encoder;
  @LocalServerPort private int port;

  private URL base;
  private Account savedAccount1;
  private Account savedAccount2;
  private Guest savedGuest1;
  private Poll savedPoll1;
  private Poll savedPoll2;

  @BeforeEach
  public void setUp() throws Exception {

    for (Vote vote : voteRepository.findAll()) {
      vote.setPollOnlyOnVoteSide(null);
      vote.setVoterOnlyOnVoteSide(null);
      voteRepository.save(vote);
    }
    voteRepository.deleteAll();
    pollRepository.deleteAll();
    userRepository.deleteAll();
    guestRepository.deleteAll();
    accountRepository.deleteAll();

    User adminUser = new User().displayName("admin");
    Account admin =
        new Account().admin(true).email("mynameisadmin").password(encoder.encode("password"));
    admin.setUserAndAddThisToUser(adminUser);

    User user1 = new User();
    Account account1 = new Account().email("oddhus").password(encoder.encode("12341234"));
    account1.setUserAndAddThisToUser(user1);

    User user2 = new User();
    Account account2 = new Account().email("email2").password(encoder.encode("12341234"));
    account2.setUserAndAddThisToUser(user2);

    Guest guest1 = new Guest().displayName("guest1");

    accountRepository.save(admin);
    savedAccount1 = accountRepository.save(account1);
    savedAccount2 = accountRepository.save(account2);
    savedGuest1 = guestRepository.save(guest1);

    Poll poll1 = new Poll().question("Question").visibilityType(PollVisibilityType.PUBLIC);
    poll1.setPollOwnerOnlyOnPollSide(savedAccount1.getUser());

    Poll poll2 =
        new Poll()
            .question("Question")
            .visibilityType(PollVisibilityType.PRIVATE)
            .startTime(new Date())
            .pollDuration(1000);
    poll2.setOwnerAndAddThisPollToOwner(savedAccount2.getUser());

    savedPoll1 = pollRepository.save(poll1);
    savedPoll2 = pollRepository.save(poll2);

    Vote vote1 = new Vote().answer(AnswerType.NO);
    vote1.setVoterAndAddThisVoteToVoter(savedAccount1.getUser());
    vote1.setPollAndAddThisVoteToPoll(savedPoll1);

    Vote vote2 = new Vote().answer(AnswerType.YES);
    vote2.setVoterAndAddThisVoteToVoter(savedAccount2.getUser());
    vote2.setPollAndAddThisVoteToPoll(savedPoll1);

    Vote vote3 = new Vote().answer(AnswerType.NO);
    vote3.setVoterAndAddThisVoteToVoter(savedGuest1);
    vote3.setPollAndAddThisVoteToPoll(savedPoll1);

    Vote vote4 = new Vote().answer(AnswerType.NO);
    vote4.setVoterAndAddThisVoteToVoter(savedGuest1);
    vote4.setPollAndAddThisVoteToPoll(savedPoll2);

    voteRepository.save(vote1);
    voteRepository.save(vote2);
    voteRepository.save(vote3);
    voteRepository.save(vote4);

    this.base = new URL("http://localhost:" + port + "/polls");
    template
        .getRestTemplate()
        .setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to
    // make PATCH request
    loginUserInTest.login("oddhus", "12341234", "/auth/signin", port, template, objectMapper);
  }

  @AfterEach
  public void tearDown() {
    for (Vote vote : voteRepository.findAll()) {
      vote.setPollOnlyOnVoteSide(null);
      vote.setVoterOnlyOnVoteSide(null);
      voteRepository.delete(vote);
    }

    pollRepository.deleteAll();
    userRepository.deleteAll();
    guestRepository.deleteAll();
    accountRepository.deleteAll();
  }

  @Test
  public void shouldGetAllPollsAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "mynameisadmin", "password", "/auth/signin", port, template, objectMapper);

    ResponseEntity<PollResponse[]> response =
        template.getForEntity(base.toString() + "/admin", PollResponse[].class);

    PollResponse[] polls = response.getBody();
    Assertions.assertNotNull(polls);
    Assertions.assertEquals(2, polls.length);
    Assertions.assertTrue(
        Arrays.stream(polls).anyMatch(poll -> savedPoll1.getId().equals(poll.getId())));
    Assertions.assertTrue(
        Arrays.stream(polls).anyMatch(poll -> savedPoll2.getId().equals(poll.getId())));
  }

  @Test
  public void shouldUpdatePollByPollId() {
    Poll poll = new Poll().question("?").visibilityType(PollVisibilityType.PRIVATE);
    CreateOrUpdatePollRequest createOrUpdatePollRequest =
        new CreateOrUpdatePollRequest().poll(poll).emails(Collections.singletonList("email2"));

    template.put(
        base.toString() + "/" + savedPoll1.getId(), createOrUpdatePollRequest, PollResponse.class);

    Optional<Poll> foundPoll = pollRepository.findById(savedPoll1.getId());

    Assertions.assertTrue(foundPoll.isPresent());
    Assertions.assertEquals(PollVisibilityType.PRIVATE, foundPoll.get().getVisibilityType());
    Assertions.assertEquals("?", foundPoll.get().getQuestion());
  }

  @Test
  public void shouldGetPollByPollId() {
    ResponseEntity<PollResponse> response =
        template.getForEntity(base.toString() + "/" + this.savedPoll1.getId(), PollResponse.class);
    PollResponse poll = response.getBody();

    Assertions.assertNotNull(poll);
    Assertions.assertEquals(savedPoll1.getId(), poll.getId());
  }

  @Test
  public void shouldNotGetPollByPollIdIfPrivateAndNotAllowed() {
    ResponseEntity<PollResponse> response =
        template.getForEntity(base.toString() + "/" + this.savedPoll2.getId(), PollResponse.class);
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void shouldGetPollByPollIdIfPrivateAndAllowed() {
    savedPoll2.getAllowedVoters().add(savedAccount1.getUser());
    pollRepository.save(savedPoll2);
    ResponseEntity<PollResponse> response =
        template.getForEntity(base.toString() + "/" + this.savedPoll2.getId(), PollResponse.class);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertEquals(savedPoll2.getId(), response.getBody().getId());
  }

  @Test
  public void shouldDeletePollByPollId() {
    template.delete(base.toString() + "/" + savedPoll1.getId().toString());
    List<Vote> votes = voteRepository.findByPoll(savedPoll1);
    Assertions.assertEquals(0, votes.size());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertTrue(pollRepository.findById(savedPoll1.getId()).isEmpty());
    Assertions.assertEquals(3, userRepository.count());
  }

  @Test
  public void shouldGetAllPublicPolls() {
    ResponseEntity<PollResponse[]> response =
        template.getForEntity(base.toString(), PollResponse[].class);
    PollResponse[] polls = response.getBody();
    Assertions.assertNotNull(polls);
    Assertions.assertEquals(1, polls.length);
  }

  @Test
  public void shouldGetAllUsersPollsAsOwner() {
    ResponseEntity<PollResponse[]> response =
        template.getForEntity(
            base.toString() + "/owner/" + savedAccount1.getUser().getId(), PollResponse[].class);
    PollResponse[] polls = response.getBody();
    Assertions.assertNotNull(polls);
    Assertions.assertEquals(1, polls.length);
  }

  @Test
  public void shouldGetAllUsersPollsAsAdmin() throws JsonProcessingException {
    loginUserInTest.login(
        "mynameisadmin", "password", "/auth/signin", port, template, objectMapper);
    ResponseEntity<PollResponse[]> response =
        template.getForEntity(
            base.toString() + "/owner/" + savedAccount1.getUser().getId(), PollResponse[].class);
    PollResponse[] polls = response.getBody();
    Assertions.assertNotNull(polls);
    Assertions.assertEquals(1, polls.length);
  }

  @Test
  public void shouldCreateNewPoll() {
    Poll poll = new Poll();
    poll.setQuestion("Banana pizza?");
    poll.setName("Poll name");
    poll.setVisibilityType(PollVisibilityType.PRIVATE);

    CreateOrUpdatePollRequest createOrUpdatePollRequest =
        new CreateOrUpdatePollRequest().poll(poll).emails(Collections.singletonList("email2"));

    ResponseEntity<PollResponse> response =
        template.postForEntity(base.toString(), createOrUpdatePollRequest, PollResponse.class);
    PollResponse postedPoll = response.getBody();

    Assertions.assertNotNull(postedPoll);
    Assertions.assertNotNull(postedPoll.getId());
    Assertions.assertEquals(postedPoll.getQuestion(), "Banana pizza?");
    Assertions.assertTrue(postedPoll.getAllowedVoters().contains("email2"));
  }

  @Test
  public void shouldActivatePoll() {
    Boolean response =
        template.patchForObject(
            base.toString() + "/" + savedPoll1.getId(), savedPoll1, Boolean.class);
    Assertions.assertEquals(response, true);
    Assertions.assertNotNull(pollRepository.findById(savedPoll1.getId()).get().getStartTime());
  }

  @Test
  public void shouldGetIfPollIsActive() {
    ResponseEntity<Boolean> response =
        template.getForEntity(
            base.toString() + "/" + this.savedPoll1.getId() + "/active", Boolean.class);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertFalse(response.getBody());

    ResponseEntity<Boolean> response2 =
        template.getForEntity(
            base.toString() + "/" + this.savedPoll2.getId() + "/active", Boolean.class);
    Assertions.assertNotNull(response2.getBody());
    Assertions.assertTrue(response2.getBody());
  }

  @Test
  public void shouldGetNumberOfYesAndNoVotes() {
    ResponseEntity<VotesResponse> response =
        template.getForEntity(
            base.toString() + "/" + this.savedPoll1.getId() + "/votes", VotesResponse.class);
    Assertions.assertEquals(1, Objects.requireNonNull(response.getBody()).getYes());
    Assertions.assertEquals(2, Objects.requireNonNull(response.getBody()).getNo());
  }

  @Test
  public void shouldNotGetNumberOfYesAndNoVotesIfPrivate() {
    ResponseEntity<String> response =
        template.getForEntity(
            base.toString() + "/" + this.savedPoll2.getId() + "/votes", String.class);
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void shouldGetNumberOfYesAndNoVotesIfPrivateAndAllowed() {
    savedPoll2.getAllowedVoters().add(savedAccount1.getUser());
    pollRepository.save(savedPoll2);

    ResponseEntity<VotesResponse> response =
        template.getForEntity(
            base.toString() + "/" + this.savedPoll2.getId() + "/votes", VotesResponse.class);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(0, Objects.requireNonNull(response.getBody()).getYes());
    Assertions.assertEquals(1, Objects.requireNonNull(response.getBody()).getNo());
  }
}
