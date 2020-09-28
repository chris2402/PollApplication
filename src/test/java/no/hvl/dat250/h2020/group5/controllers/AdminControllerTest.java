package no.hvl.dat250.h2020.group5.controllers;

import com.google.gson.JsonObject;
import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.dao.GuestRepository;
import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @LocalServerPort private int port;

    private URL base;

    private User user1;
    private Poll poll1;

    @Autowired TestRestTemplate template;

    @Autowired AdminController adminController;

    @Autowired PollRepository pollRepository;

    @Autowired UserRepository userRepository;

    @Autowired GuestRepository guestRepository;

    @BeforeEach
    public void setUp() throws Exception {
        user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        Guest guest1 = new Guest();
        guest1.setUsername("guest1");

        poll1 = new Poll();
        poll1.setQuestion("Question");
        poll1.setPollOwner(user1);
        Poll poll2 = new Poll();
        poll2.setQuestion("Question");
        poll2.setPollOwner(user2);

        Vote vote1 = new Vote();
        vote1.setVoter(user1);
        vote1.setPoll(poll1);
        Vote vote2 = new Vote();
        vote2.setVoter(guest1);
        vote2.setPoll(poll1);

        pollRepository.save(poll1);
        pollRepository.save(poll2);
        userRepository.save(user1);
        userRepository.save(user2);
        guestRepository.save(guest1);

        this.base = new URL("http://localhost:" + port + "/admin");
        template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to make PATCH request
    }

    @Test
    public void shouldGetAllUsers() {
        ResponseEntity<User[]> response =
                template.getForEntity(base.toString() + "/users", User[].class);
        User[] users = response.getBody();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.length);
        Assertions.assertEquals(users[0], userRepository.findById(user1.getId()).get());
    }

    @Test
    public void shouldGetUserById() {

        ResponseEntity<User> response =
                template.getForEntity(
                        base.toString() + "/users/" + user1.getId().toString(), User.class);
        User user = response.getBody();
        Assertions.assertEquals(user, userRepository.findById(user1.getId()).get());
    }

    @Test
    public void shouldUpdateUserById() {
        UpdateUserRequest update = new UpdateUserRequest();
        update.setUsername("Nytt navn");

        Boolean response =
                template.patchForObject(
                        base.toString() + "/users/" + user1.getId().toString(),
                        update,
                        Boolean.class);
        Assertions.assertTrue(response);
        Assertions.assertEquals(
                update.getUsername(), userRepository.findById(user1.getId()).get().getUsername());
    }

    @Test
    public void shouldDeleteUserById() {
        template.delete(base.toString() + "/users/" + user1.getId().toString());
        Assertions.assertTrue(userRepository.findById(user1.getId()).isEmpty());
    }

    @Test
    public void shouldGetAllUsersPolls() {
        ResponseEntity<Poll[]> response =
                template.getForEntity(
                        base.toString() + "/users/" + user1.getId().toString() + "/polls",
                        Poll[].class);
        Poll[] polls = response.getBody();
        Assertions.assertNotNull(polls);
        Assertions.assertEquals(1, polls.length);
        Assertions.assertEquals(polls[0], pollRepository.findById(poll1.getId()).get());
    }

    @Test
    public void shouldGetAllPolls() {
        ResponseEntity<Poll[]> response =
                template.getForEntity(base.toString() + "/polls", Poll[].class);
        Poll[] polls = response.getBody();
        Assertions.assertNotNull(polls);
        Assertions.assertEquals(2, polls.length);
    }

    @Test
    public void shouldPollByPollId() {
        ResponseEntity<Poll> response =
                template.getForEntity(base.toString() + "/polls/" + poll1.getId(), Poll.class);
        Poll poll = response.getBody();
        Assertions.assertEquals(poll1, poll);
    }

    @Test
    public void shouldDeletePollByPollId() {
        template.delete(base.toString() + "/polls/" + poll1.getId().toString());
        Assertions.assertTrue(pollRepository.findById(poll1.getId()).isEmpty());
    }
}
