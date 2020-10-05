package no.hvl.dat250.h2020.group5.repostories;

import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class PollRepositoryTest {

  @Autowired PollRepository pollRepository;
  @Autowired VoteRepository voteRepository;
  @Autowired UserRepository userRepository;

  private Poll poll;
  private List<Vote> votes;
  private User user;

  @BeforeEach
  public void setUp() {
    this.user = new User();
    this.poll = new Poll();
    this.votes = Arrays.asList(new Vote(), new Vote(), new Vote());

    poll.addVoteAndSetThisPollInVote(new Vote());
    poll.addVoteAndSetThisPollInVote(new Vote());
    poll.addVoteAndSetThisPollInVote(new Vote());
    user.addPoll(poll);

    userRepository.save(user);
  }

  @Test
  public void shouldPersistOnePollTest() {
    Assertions.assertEquals(1, pollRepository.count());
    Assertions.assertEquals(poll.getId(), pollRepository.findAll().get(0).getId());
  }

  @Test
  public void shouldHaveThreeVotesWhenThreeVotesAreAddedTest() {
    Optional<Poll> pollWithVotes = pollRepository.findById(poll.getId());
    Assertions.assertEquals(3, pollWithVotes.get().getVotes().size());
  }

  @Test
  public void shouldDeletePollTest() {
    user.deletePoll(poll);
    Assertions.assertEquals(0, pollRepository.count());
  }

  @Test
  public void shouldPersistVotesWhenAddedToPollTest() {
    Assertions.assertEquals(3, voteRepository.count());
    Assertions.assertNotNull(voteRepository.findAll().get(0).getId());
    Assertions.assertNotNull(poll.getVotes().get(0).getId());
  }

  @Test
  public void shouldDeleteVotesWhenDeletingPollTest() {
    user.deletePoll(poll);
    Assertions.assertEquals(0, voteRepository.count());
  }

  @Test
  public void shouldOnlyDeleteVotesLinkedToPollWhenDeletingPollTest() {
    Vote voteNotLinkedToPoll = new Vote();
    voteRepository.save(voteNotLinkedToPoll);
    user.deletePoll(poll);
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertEquals(voteNotLinkedToPoll.getId(), voteRepository.findAll().get(0).getId());
  }

  @Test
  public void shouldNotDeleteUserWhenDeletingPollTest() {
    user.deletePoll(poll);
    Assertions.assertEquals(user.getId(), userRepository.findAll().get(0).getId());
  }

  @Test
  public void shouldReturnPublicPollsTest() {
    pollRepository.saveAll(
        Arrays.asList(
            new Poll().visibilityType(PollVisibilityType.PUBLIC),
            new Poll().visibilityType(PollVisibilityType.PUBLIC),
            new Poll().visibilityType(PollVisibilityType.PRIVATE)));
    Assertions.assertEquals(
        2, pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC).size());
    Assertions.assertEquals(
        1, pollRepository.findAllByVisibilityType(PollVisibilityType.PRIVATE).size());
  }

  @Test
  public void shouldGiveIdToVote() {
    Poll newPoll = new Poll();
    Vote newVote = new Vote();
    Poll savedPoll = pollRepository.save(newPoll);

    savedPoll.addVoteAndSetThisPollInVote(newVote);

    pollRepository.save(savedPoll);

    Assertions.assertEquals(4, voteRepository.count());
    Assertions.assertEquals(1, pollRepository.findById(savedPoll.getId()).get().getVotes().size());
    Assertions.assertEquals(1, voteRepository.findByPoll(savedPoll).size());
    Assertions.assertNotNull(voteRepository.findByPoll(savedPoll).get(0).getId());
  }
}
