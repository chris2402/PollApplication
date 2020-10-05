package no.hvl.dat250.h2020.group5.repostories;

import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class UserRepositoryTest {

  @Autowired VoteRepository voteRepository;

  @Autowired PollRepository pollRepository;

  @Autowired VoterRepository voterRepository;

  @Autowired UserRepository userRepository;

  private User savedUser;

  @BeforeEach
  public void setUp() {
    User user = new User();
    Poll poll = new Poll();
    Vote vote = new Vote().answer(AnswerType.YES);

    user.addVote(vote);
    user.addPoll(poll);
    savedUser = userRepository.save(user);
  }

  @Test
  public void shouldPersistUserTest() {
    Assertions.assertEquals(1, userRepository.count());
    Assertions.assertNotNull(savedUser.getId());
  }

  @Test
  public void shouldPersistUserAndPollTest() {
    Assertions.assertEquals(1, pollRepository.count());
    Assertions.assertNotNull(pollRepository.findAll().get(0).getId());
    Assertions.assertNotNull(pollRepository.findAllByPollOwner(savedUser).get(0).getId());
  }

  @Test
  public void shouldPersistUserAndVoteTest() {
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertNotNull(voteRepository.findAll().get(0).getId());
    Assertions.assertNotNull(voteRepository.findByVoter(savedUser).get(0).getId());
  }

  @Test
  public void shouldAddNewVoteToUser() {
    Vote vote = new Vote();

    Optional<User> user = userRepository.findById(savedUser.getId());
    Assertions.assertTrue(user.isPresent());

    user.get().addVote(vote);
    userRepository.save(user.get());

    Assertions.assertEquals(2, voteRepository.count());
    Assertions.assertEquals(2, userRepository.findById(savedUser.getId()).get().getVotes().size());
  }

  @Test
  public void shouldAddNewPollToUser() {
    Poll poll = new Poll();

    Optional<User> user = userRepository.findById(savedUser.getId());
    Assertions.assertTrue(user.isPresent());

    user.get().addPoll(poll);
    userRepository.save(user.get());

    Assertions.assertEquals(2, pollRepository.count());
    Assertions.assertEquals(
        2, userRepository.findById(savedUser.getId()).get().getUserPolls().size());
  }

  @Test
  public void shouldDeletePollTest() {
    Poll poll = pollRepository.findAllByPollOwner(savedUser).get(0);
    savedUser.deletePoll(poll);
    Assertions.assertEquals(0, pollRepository.count());
  }

  @Test
  public void shouldDeletePollNotUser() {
    Poll poll = pollRepository.findAllByPollOwner(savedUser).get(0);
    pollRepository.delete(poll);
    Assertions.assertEquals(1, userRepository.count());
  }

  @Test
  public void shouldDeletePollAndNotVotesWhenDeletingUser() {
    userRepository.delete(savedUser);

    Assertions.assertEquals(0, userRepository.count());
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(1, pollRepository.count());
  }
}
