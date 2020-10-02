package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PollControllerTest {

    @LocalServerPort private int port;

    @Autowired TestRestTemplate template;

    @Autowired PollController PollController;

    @Autowired PollRepository pollRepository;

    @Autowired UserRepository userRepository;

    @Autowired GuestRepository guestRepository;

    @Autowired VoteRepository voteRepository;

    private URL base;
    private User user1;
    private Poll poll1;

    @BeforeEach
    public void setUp() throws Exception {
        pollRepository.deleteAll();
        userRepository.deleteAll();
        guestRepository.deleteAll();

        user1 = new User();
        user1.setUsername("Admin");
        user1.setIsAdmin(true);

        User user2 = new User();
        user2.setUsername("Not admin");

        Guest guest1 = new Guest();
        guest1.setUsername("guest1");

        poll1 = new Poll();
        poll1.setQuestion("Question");
        poll1.setPollOwner(user1);
        poll1.setVisibilityType(PollVisibilityType.PUBLIC);

        Poll poll2 = new Poll();
        poll2.setQuestion("Question");
        poll2.setPollOwner(user2);
        poll2.setVisibilityType(PollVisibilityType.PRIVATE);

        poll1 = pollRepository.save(poll1);
        pollRepository.save(poll2);
        user1 = userRepository.save(user1);
        guestRepository.save(guest1);

        Vote vote1 = new Vote();
        vote1.setVoter(user1);
        vote1.setPoll(poll1);
        vote1.setAnswer(AnswerType.NO);
        vote1.setId((long) 123123);

        Vote vote2 = new Vote();
        vote2.setVoter(user2);
        vote2.setPoll(poll1);
        vote2.setId((long) 321423412);
        vote2.setAnswer(AnswerType.YES);

        Vote vote3 = new Vote();
        vote3.setVoter(guest1);
        vote3.setPoll(poll1);
        vote3.setId((long) 5644);
        vote3.setAnswer(AnswerType.NO);

        Vote vote4 = new Vote();
        vote4.setVoter(guest1);
        vote4.setPoll(poll1);
        vote4.setId((long) 12352);
        vote4.setAnswer(AnswerType.NO);

        voteRepository.save(vote1);
        voteRepository.save(vote2);
        voteRepository.save(vote3);
        voteRepository.save(vote4);

        this.base = new URL("http://localhost:" + port + "/polls");
        template.getRestTemplate()
                .setRequestFactory(
                        new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to
        // make PATCH request
    }

    @Test
    public void shouldGetAllPolls() {
        ResponseEntity<Poll[]> response =
                template.getForEntity(
                        base.toString()  + "/admin/" + this.user1.getId().toString() + "/polls",
                        Poll[].class);
        Poll[] polls = response.getBody();
        Assertions.assertNotNull(polls);
        Assertions.assertEquals(2, polls.length);
    }

    @Test
    public void shouldGetPollByPollId() {
        ResponseEntity<Poll> response =
                template.getForEntity(base.toString() + "/" + this.poll1.getId(), Poll.class);
        Poll poll = response.getBody();

        Assertions.assertEquals(poll1, poll);
    }

    @Test
    public void shouldDeletePollByPollId() {
        template.delete(base.toString() + "/" + poll1.getId().toString() + "/" + user1.getId().toString());
        List<Vote> votes = voteRepository.findByPoll(poll1);
        Assertions.assertEquals(votes.size(), 0);
        Assertions.assertTrue(pollRepository.findById(poll1.getId()).isEmpty());
    }

    @Test
    public void shouldGetAllPublicPolls() {
        ResponseEntity<Poll[]> response = template.getForEntity(base.toString(), Poll[].class);
        Poll[] polls = response.getBody();
        Assertions.assertNotNull(polls);
        Assertions.assertEquals(1, polls.length);
    }

    @Test
    public void shouldCreateNewPoll() {
        Poll poll = new Poll();
        poll.setQuestion("Banana pizza?");
        poll.setName("Poll name");
        ResponseEntity<Poll> response =
                template.postForEntity(
                        base.toString() + "/" + this.user1.getId().toString(), poll, Poll.class);
        Poll postedPoll = response.getBody();
        Assertions.assertNotNull(poll);
        assert postedPoll != null;
        Assertions.assertEquals(postedPoll.getQuestion(), "Banana pizza?");
    }

    @Test
    public void shouldActivatePoll() {
        Boolean response =
                template.patchForObject(
                        base.toString() + "/" + poll1.getId().toString(), poll1, Boolean.class);
        Assertions.assertEquals(response, true);
    }

    @Test
    public void shouldGetIfPollIsActive() {
        ResponseEntity<Boolean> response =
                template.getForEntity(
                        base.toString() + "/" + this.poll1.getId() + "/active", Boolean.class);
        Assertions.assertEquals(response.getBody(), false);
    }

    @Test
    public void shouldGetNumberOfYesAndNoVotes() {
        ResponseEntity<VotesResponse> response =
                template.getForEntity(
                        base.toString() + "/" + this.poll1.getId() + "/votes", VotesResponse.class);
        Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getYes(), 1);
        Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getNo(), 3);
    }
}
