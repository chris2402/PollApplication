package no.hvl.dat250.h2020.group5.integrationtests;

import net.jcip.annotations.NotThreadSafe;
import no.hvl.dat250.h2020.group5.controllers.PollController;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@NotThreadSafe
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PollControllerIT {

  @Autowired TestRestTemplate template;
  @Autowired PollController pollController;
  @Autowired PollRepository pollRepository;
  @Autowired UserRepository userRepository;
  @Autowired GuestRepository guestRepository;
  @Autowired VoteRepository voteRepository;
  @LocalServerPort private int port;

  private URL base;
  private User savedUser1;
  private User savedUser2;
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

    User user1 = new User().userName("Admin").admin(true);
    User user2 = new User().userName("Not admin");
    Guest guest1 = new Guest().username("guest1");

    savedUser1 = userRepository.save(user1);
    savedUser2 = userRepository.save(user2);
    savedGuest1 = guestRepository.save(guest1);

    Poll poll1 = new Poll().question("Question").visibilityType(PollVisibilityType.PUBLIC);
    poll1.setOwnerAndAddThisPollToOwner(savedUser1);

    Poll poll2 =
        new Poll()
            .question("Question")
            .visibilityType(PollVisibilityType.PRIVATE)
            .startTime(new Date())
            .pollDuration(1000);
    poll2.setOwnerAndAddThisPollToOwner(savedUser2);

    savedPoll1 = pollRepository.save(poll1);
    savedPoll2 = pollRepository.save(poll2);

    Vote vote1 = new Vote().answer(AnswerType.NO);
    vote1.setVoterAndAddThisVoteToVoter(savedUser1);
    vote1.setPollAndAddThisVoteToPoll(savedPoll1);

    Vote vote2 = new Vote().answer(AnswerType.YES);
    vote2.setVoterAndAddThisVoteToVoter(savedUser2);
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
  }

  @Test
  public void test(){
    String loginUrl = "http://localhost:" + port +"/auth/signin";
    String username = "username";
    String password = "password";

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.set("username", username);
    form.set("password", password);
    ResponseEntity<String> loginResponse = template.postForEntity(
            loginUrl,
            new HttpEntity<>(form, new HttpHeaders()),
            String.class);
    String cookie = loginResponse.getHeaders().get("Set-Cookie").get(0);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", cookie);
    ResponseEntity<String> responseFromSecuredEndPoint = testRestTemplate.exchange(securedUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

    assertEquals(responseFromSecuredEndPoint.getStatusCode(), HttpStatus.OK);
    assertTrue(responseFromSecuredEndPoint.getBody().contains("Hello World!"));
  }

  @Test
  public void shouldGetAllPolls() {
    ResponseEntity<PollResponse[]> response =
        template.getForEntity(
            base.toString() + "/admin/" + this.savedUser1.getId().toString() + "/polls",
            PollResponse[].class);
    PollResponse[] polls = response.getBody();
    Assertions.assertNotNull(polls);
    Assertions.assertEquals(2, polls.length);
    Assertions.assertTrue(
        Arrays.stream(polls).anyMatch(poll -> savedPoll1.getId().equals(poll.getId())));
    Assertions.assertTrue(
        Arrays.stream(polls).anyMatch(poll -> savedPoll2.getId().equals(poll.getId())));
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
  public void shouldDeletePollByPollId() {
    template.delete(
        base.toString()
            + "/"
            + savedPoll1.getId().toString()
            + "/"
            + savedUser1.getId().toString());
    List<Vote> votes = voteRepository.findByPoll(savedPoll1);
    Assertions.assertEquals(0, votes.size());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertTrue(pollRepository.findById(savedPoll1.getId()).isEmpty());
    Assertions.assertEquals(2, userRepository.count());
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
  public void shouldCreateNewPoll() {
    Poll poll = new Poll();
    poll.setQuestion("Banana pizza?");
    poll.setName("Poll name");

    ResponseEntity<PollResponse> response =
        template.postForEntity(
            base.toString() + "/" + this.savedUser1.getId(), poll, PollResponse.class);
    PollResponse postedPoll = response.getBody();

    Assertions.assertNotNull(postedPoll);
    Assertions.assertNotNull(postedPoll.getId());
    Assertions.assertEquals(postedPoll.getQuestion(), "Banana pizza?");
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
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getYes(), 1);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getNo(), 2);
  }
}
