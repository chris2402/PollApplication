package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.dao.GuestRepository;
import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
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
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @LocalServerPort private int port;

    @Autowired TestRestTemplate template;

    @Autowired AdminController adminController;

    @Autowired PollRepository pollRepository;

    @Autowired UserRepository userRepository;

    @Autowired GuestRepository guestRepository;

    private URL base;
    private User user1;
    private Poll poll1;

    @BeforeEach
    public void setUp() throws Exception {
        pollRepository.deleteAll();
        userRepository.deleteAll();
        guestRepository.deleteAll();

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
        template.getRestTemplate()
                .setRequestFactory(
                        new HttpComponentsClientHttpRequestFactory()); // Necessary to be able to
        // make PATCH request
    }

    @Test
    public void shouldGetAllUsers() {
        ResponseEntity<UserResponse[]> response =
                template.getForEntity(base.toString() + "/users", UserResponse[].class);
        UserResponse[] users = response.getBody();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.length);
        Assertions.assertEquals(users[0].getId(), userRepository.findById(user1.getId()).get().getId());
    }

    @Test
    public void shouldGetUserById() {

        ResponseEntity<UserResponse> response =
                template.getForEntity(
                        base.toString() + "/users/" + user1.getId().toString(), UserResponse.class);
        UserResponse user = response.getBody();
        Optional<User> userFromRepository = userRepository.findById(user1.getId());
        Assertions.assertNotNull(user);
        Assertions.assertTrue(userFromRepository.isPresent());
        Assertions.assertEquals(user.getId(), userFromRepository.get().getId());
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
        ResponseEntity<UserResponse[]> response =
                template.getForEntity(
                        base.toString() + "/users/" + user1.getId().toString() + "/polls",
                        UserResponse[].class);
        UserResponse[] polls = response.getBody();
        Optional<Poll> fromPollRepository = pollRepository.findById(poll1.getId());
        Assertions.assertTrue(fromPollRepository.isPresent());
        Assertions.assertNotNull(polls);
        Assertions.assertEquals(1, polls.length);
        Assertions.assertEquals(polls[0].getId(), fromPollRepository.get().getId());
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
