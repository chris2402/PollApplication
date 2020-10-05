package no.hvl.dat250.h2020.group5.repostories;

import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class VoterRepositoryTest {
  @Autowired VoteRepository voteRepository;

  @Autowired GuestRepository guestRepository;

  @Autowired UserRepository userRepository;

  @Autowired VoterRepository voterRepository;

  private Voter savedVoterUser;
  private Voter savedVoterGuest;

  @BeforeEach
  public void setUp() {
    Voter guest = new Guest();
    Voter user = new User();
    Vote vote1 = new Vote().answer(AnswerType.YES);
    Vote vote2 = new Vote().answer(AnswerType.NO);

    guest.addVoteAndSetThisVoterInVote(vote1);
    user.addVoteAndSetThisVoterInVote(vote2);
    savedVoterGuest = voterRepository.save(guest);
    savedVoterUser = voterRepository.save(user);
  }

  @Test
  public void shouldGetAllVoters() {
    Assertions.assertEquals(2, voterRepository.findAll().size());
  }

  @Test
  public void shouldFindVoterGuest() {
    Assertions.assertEquals(
        savedVoterGuest, voterRepository.findById(savedVoterGuest.getId()).get());
  }

  @Test
  public void shouldFindVoterUser() {
    Assertions.assertEquals(savedVoterUser, voterRepository.findById(savedVoterUser.getId()).get());
  }

  @Test
  public void shouldSaveNewVoteToVoterWhenSavingVoter() {
    Vote vote = new Vote();
    savedVoterUser.addVoteAndSetThisVoterInVote(vote);
    voterRepository.save(savedVoterUser);

    Assertions.assertEquals(3, voteRepository.count());
    Assertions.assertEquals(2, voteRepository.findByVoter(savedVoterUser).size());
    Assertions.assertEquals(
        2, voterRepository.findById(savedVoterUser.getId()).get().getVotes().size());
  }

  @Test
  public void shouldDeleteVoterAfterSettingAllVotesToNull() {
    for (Vote vote : voteRepository.findByVoter(savedVoterUser)) {
      vote.setVoterAndAddThisVoteToVoter(null);
      voteRepository.save(vote);
    }

    voterRepository.delete(savedVoterUser);

    Assertions.assertEquals(1, voterRepository.count());
    Assertions.assertEquals(2, voteRepository.count());
  }
}
