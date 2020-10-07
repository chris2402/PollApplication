package no.hvl.dat250.h2020.group5.integrationtests;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

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
  @LocalServerPort private int port;
  private URL base;
  private User user;

  private Poll savedPoll;
  private User savedUser;
  private Guest savedGuest;

  @BeforeEach
  public void setUp() throws MalformedURLException {
    this.base = new URL("http://localhost:" + port + "/votes");

    Poll poll =
        new Poll()
            .visibilityType(PollVisibilityType.PUBLIC)
            .pollDuration(1000)
            .name("my first poll")
            .startTime(Date.from(Instant.now()));
    this.savedPoll = pollRepository.save(poll);
    User user = new User();
    this.savedUser = userRepository.save(user);
    Guest guest = new Guest();
    this.savedGuest = guestRepository.save(guest);
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

  @Test
  public void shouldVoteByUserTest() {
    CastVoteRequest voteRequest = new CastVoteRequest();
    voteRequest.setPollId(savedPoll.getId());
    voteRequest.setUserId(savedUser.getId());
    voteRequest.setVote("YES");
    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(base.toString(), voteRequest, Vote.class);
    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote.getId());
    Assertions.assertEquals(AnswerType.YES, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(savedUser.getId(), voteRepository.findAll().get(0).getVoter().getId());
  }

  @Test
  public void shouldVoteByGuestTest() {
    CastVoteRequest voteRequest = new CastVoteRequest();
    voteRequest.setPollId(savedPoll.getId());
    voteRequest.setUserId(savedGuest.getId());
    voteRequest.setVote("NO");
    ResponseEntity<Vote> response =
        testRestTemplate.postForEntity(base.toString(), voteRequest, Vote.class);
    Vote savedVote = response.getBody();
    Assertions.assertNotNull(savedVote.getId());
    Assertions.assertEquals(AnswerType.NO, savedVote.getAnswer());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(savedPoll.getId(), voteRepository.findAll().get(0).getPoll().getId());
    Assertions.assertEquals(savedGuest.getId(), voteRepository.findAll().get(0).getVoter().getId());
  }
}
