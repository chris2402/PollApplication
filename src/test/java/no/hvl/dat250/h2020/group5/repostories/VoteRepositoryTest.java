package no.hvl.dat250.h2020.group5.repostories;

import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class VoteRepositoryTest {

  @Autowired VoteRepository voteRepository;

  @Autowired PollRepository pollRepository;

  @Autowired VoterRepository voterRepository;

  private Vote vote;
  private Poll poll;
  private Voter voter;

  @BeforeEach
  public void setUp() {
    this.vote = new Vote();
    this.poll = new Poll();
    this.voter = new Guest();

    pollRepository.save(poll);

    vote.setAnswer(AnswerType.YES);
    vote.setPoll(poll);
    voter.addVote(vote);

    voteRepository.save(vote);
    voterRepository.save(voter);
  }

  @Test
  public void shouldPersistAVoteTest() {
    Assertions.assertEquals(1, voteRepository.count());
  }

  @Test
  public void shouldFindVoteByPollAndAnswerTest() {
    Assertions.assertEquals(vote, voteRepository.findByPollAndAnswer(poll, AnswerType.YES).get(0));
  }

  @Test
  public void shouldFindVoteByPollAndOwnerTest() {
    Assertions.assertEquals(vote, voteRepository.findByVoterAndPoll(voter, poll).get());
  }

  @Test
  public void shouldFindVoteByOwnerTest() {
    Assertions.assertEquals(vote, voteRepository.findByVoter(voter).get(0));
  }

  @Test
  public void shouldNotDeleteAVoteWhenVoterIsDeletedTest() {
    voterRepository.deleteById(voter.getId());
    Assertions.assertEquals(1, voteRepository.count());
  }

  @Test
  public void shouldAddVoteToPollTest() {
    Vote newVote = new Vote();
    Poll newPoll = new Poll();
    Poll savedPoll = pollRepository.save(newPoll);
    newVote.setPoll(newPoll);
    voteRepository.save(newVote);
    Assertions.assertEquals(1, pollRepository.findById(savedPoll.getId()).get().getVotes().size());
  }

  @Test
  public void shouldAddAllVotesToPollTest() {
    Poll newPoll = new Poll();
    Poll savedPoll = pollRepository.save(newPoll);

    Vote newVote = new Vote();
    Vote newVote2 = new Vote();
    // Need to save votes first in order to give an ID
    voteRepository.saveAll(new ArrayList<>(Arrays.asList(newVote, newVote2)));

    newVote.setPoll(newPoll);
    newVote2.setPoll(newPoll);

    voteRepository.saveAll(new ArrayList<>(Arrays.asList(newVote, newVote2)));

    Assertions.assertEquals(2, pollRepository.findById(savedPoll.getId()).get().getVotes().size());
  }
}
