package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PollServiceTest {

  @InjectMocks PollService pollService;

  @Mock PollRepository pollRepository;

  @Mock UserRepository userRepository;

  @Mock VoteRepository voteRepository;

  private User user;
  private Poll poll;
  private Vote vote;
  private List<Poll> polls;

  @BeforeEach
  public void setUp() {
    this.user = new User();
    this.poll = new Poll();
    this.vote = new Vote().answer(AnswerType.YES);

    user.setId(1L);
    poll.setId(2L);
    vote.setPollAndAddThisVoteToPoll(poll);
    vote.setVoterAndAddThisVoteToVoter(user);

    this.polls =
        Arrays.asList(poll.pollOwner(user), new Poll().pollOwner(user), new Poll().pollOwner(user));

    when(pollRepository.findAllByPollOwner(user)).thenReturn(polls);
  }

  @Test
  public void shouldCreateANewPollTest() {
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    pollService.createPoll(poll, user.getId());
    verify(pollRepository, times(1)).save(poll);
  }

  @Test
  public void shouldNotCreateANewPollIfUserIsNotPresentTest() {
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.empty());
    pollService.createPoll(poll, user.getId());
    verify(pollRepository, times(0)).save(poll);
  }

  @Test
  public void shouldDeleteAPollWhenUserIsOwnerTest() {
    poll.setOwnerAndAddThisPollToOwner(user);
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    when(voteRepository.findByPoll(poll)).thenReturn(Collections.singletonList(vote));

    pollService.deletePoll(poll.getId(), user.getId());

    verify(pollRepository, times(1)).delete(poll);
    verify(voteRepository, times(1)).delete(vote);
    Assertions.assertNull(vote.getPoll());
    Assertions.assertNull(vote.getVoter());
  }

  @Test
  public void shouldDeleteAPollWhenUserIsAdminTest() {
    User pollOwner = new User();
    pollOwner.setId(3L);
    poll.setOwnerAndAddThisPollToOwner(pollOwner);
    user.setIsAdmin(true);

    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    when(voteRepository.findByPoll(poll)).thenReturn(Collections.singletonList(vote));

    pollService.deletePoll(poll.getId(), user.getId());

    verify(pollRepository, times(1)).delete(poll);
  }

  @Test
  public void shouldNotDeletePollWhenUserIsNotOwnerOrAdminTest() {
    User pollOwner = new User();
    pollOwner.setId(3L);
    poll.setOwnerAndAddThisPollToOwner(pollOwner);

    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    pollService.deletePoll(poll.getId(), user.getId());
    verify(pollRepository, times(0)).delete(poll);
  }

  @Test
  public void shouldReturnAllPublicPollsTest() {
    when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
        .thenReturn(
            Arrays.asList(
                new Poll().visibilityType(PollVisibilityType.PUBLIC),
                new Poll().visibilityType(PollVisibilityType.PUBLIC),
                new Poll().visibilityType(PollVisibilityType.PUBLIC)));
    Assertions.assertEquals(3, pollService.getAllPublicPolls().size());
    Assertions.assertEquals(
        PollVisibilityType.PUBLIC, pollService.getAllPublicPolls().get(0).getVisibilityType());
  }

  @Test
  public void shouldGetPollResultTest() {
    poll.addVoteAndSetThisPollInVote(new Vote().answer(AnswerType.YES));
    poll.addVoteAndSetThisPollInVote(new Vote().answer(AnswerType.YES));
    poll.addVoteAndSetThisPollInVote(new Vote().answer(AnswerType.NO));

    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    VotesResponse votes = pollService.getNumberOfVotes(poll.getId());
    Assertions.assertEquals(1, votes.getNo());
    Assertions.assertEquals(3, votes.getYes());
  }

  @Test
  public void shouldActivateAPollTest() {
    poll.setPollDuration(10000000);
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    pollService.activatePoll(poll.getId());
    Assertions.assertNotNull(poll.getStartTime());
    Assertions.assertTrue(pollService.isActivated(poll.getId()));
  }

  @Test
  public void shouldGetAllPollsByOwnerTest() {
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));

    List<PollResponse> pollsFromService = pollService.getUserPollsAsOwner(user.getId());

    Assertions.assertEquals(3, pollsFromService.size());
  }

  @Test
  public void shouldOnlyGetAllPollsToUserWhenAdminTest() {
    User admin = new User();
    User notAdmin = new User();
    notAdmin.setId(6L);
    admin.setId(5L);
    admin.setIsAdmin(true);
    when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));
    when(userRepository.findById(notAdmin.getId())).thenReturn(java.util.Optional.of(notAdmin));

    List<PollResponse> pollsFromServiceAsAdmin =
        pollService.getUserPollsAsAdmin(user.getId(), admin.getId());
    List<PollResponse> pollsFromServiceNotAdmin =
        pollService.getUserPollsAsAdmin(user.getId(), notAdmin.getId());

    Assertions.assertEquals(3, pollsFromServiceAsAdmin.size());
    Assertions.assertNull(pollsFromServiceNotAdmin);
  }

  @Test
  public void shouldGiveFinishedAndPublicPollsTest() {
    this.poll.setStartTime(Date.from(Instant.now()));
    this.poll.setPollDuration(0);
    when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
        .thenReturn(Arrays.asList(this.poll));
    Assertions.assertEquals(1, pollService.getAllFinishedPublicPolls().size());
  }

  @Test
  public void shouldNotBeFinishedWhenNotActivated() {
    this.poll.setStartTime(null);
    this.poll.setPollDuration(1);
    when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
        .thenReturn(Arrays.asList(this.poll));
    Assertions.assertEquals(0, pollService.getAllFinishedPublicPolls().size());
  }
}
