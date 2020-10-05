package no.hvl.dat250.h2020.group5.repostories;

import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Optional;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class GuestRepositoryTest {
  @Autowired VoteRepository voteRepository;

  @Autowired GuestRepository guestRepository;

  private Guest savedGuest;

  @BeforeEach
  public void setUp() {
    Guest guest = new Guest();
    Vote vote = new Vote().answer(AnswerType.YES);
    vote.setVoterAndAddThisVoteToVoter(guest);
    voteRepository.save(vote);

    savedGuest = guestRepository.save(guest);
  }

  @Test
  public void shouldPersistGuestTest() {
    Assertions.assertEquals(1, guestRepository.count());
    Assertions.assertNotNull(savedGuest.getId());
  }

  @Test
  public void shouldPersistGuestAndVoteTest() {
    Assertions.assertEquals(1, voteRepository.count());
    Assertions.assertNotNull(voteRepository.findAll().get(0).getId());
    Assertions.assertEquals(1, voteRepository.findByVoter(savedGuest).size());
  }

  @Test
  public void shouldSaveNewVoteToGuestWhenSavingGuest() {
    Vote vote = new Vote().answer(AnswerType.NO);

    Optional<Guest> guest = guestRepository.findById(savedGuest.getId());
    Assertions.assertTrue(guest.isPresent());

    guest.get().addVoteAndSetThisVoterInVote(vote);
    guestRepository.save(guest.get());

    Assertions.assertEquals(2, voteRepository.count());
    Assertions.assertEquals(2, voteRepository.findByVoter(savedGuest).size());
    Assertions.assertEquals(
        2, guestRepository.findById(savedGuest.getId()).get().getVotes().size());
  }

  @Test
  public void shouldGetAllGuests() {
    guestRepository.save(new Guest());
    Assertions.assertEquals(2, guestRepository.findAll().size());
  }

  @Test
  public void shouldSaveAllGuests() {
    guestRepository.saveAll(Arrays.asList(new Guest(), new Guest()));
    Assertions.assertEquals(3, guestRepository.findAll().size());
  }

  @Test
  public void shouldFindById() {
    Assertions.assertEquals(savedGuest, guestRepository.findById(savedGuest.getId()).get());
  }
}
