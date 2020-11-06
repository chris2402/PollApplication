package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.CreateOrUpdatePollRequest;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PollServiceTest {

  @InjectMocks PollService pollService;

  @Mock PollRepository pollRepository;

  @Mock UserRepository userRepository;

  @Mock VoteRepository voteRepository;

  private User user;
  private User user2;
  private CreateOrUpdatePollRequest createOrUpdatePollRequest;
  private Poll poll;
  private Vote vote;
  private List<Poll> polls;

  @BeforeEach
  public void setUp() {
    user = new User().email("email").password("password");
    ;
    user.setId(UUID.randomUUID());

    user2 = new User().email("account2").password("password");
    user2.setId(UUID.randomUUID());

    poll =
        new Poll().name("pollname").question("question").visibilityType(PollVisibilityType.PUBLIC);
    poll.setOwnerAndAddThisPollToOwner(user);
    poll.setId(2L);
    createOrUpdatePollRequest = new CreateOrUpdatePollRequest().poll(poll);

    this.vote = new Vote().answer(AnswerType.YES);
    vote.setPollAndAddThisVoteToPoll(poll);
    vote.setVoterAndAddThisVoteToVoter(user);

    this.polls =
        Arrays.asList(poll.pollOwner(user), new Poll().pollOwner(user), new Poll().pollOwner(user));

    when(pollRepository.findAllByPollOwner(user)).thenReturn(polls);
  }

  @Test
  public void shouldCreateANewPollTest() {
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    pollService.createPoll(createOrUpdatePollRequest, user.getId());
    verify(pollRepository, times(1)).save(poll);
  }

  @Test
  public void shouldCreateANewWithAllowedUsersPollTest() {
    User user1 = new User().email("email1");
    User user2 = new User().email("email2");

    createOrUpdatePollRequest.emails(Arrays.asList(user1.getEmail(), user2.getEmail()));
    createOrUpdatePollRequest.getPoll().visibilityType(PollVisibilityType.PRIVATE);

    when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    pollService.createPoll(createOrUpdatePollRequest, user.getId());
    verify(userRepository, times(2)).findByEmail(anyString());
    verify(pollRepository, times(1)).save(poll);
  }

  @Test
  public void shouldNotCreateANewPollIfUserIsNotPresentTest() {
    when(userRepository.findById(user.getId())).thenThrow(NotFoundException.class);
    Assertions.assertThrows(
        NotFoundException.class,
        () -> pollService.createPoll(createOrUpdatePollRequest, user.getId()));
  }

  @Test
  public void shouldUpdatePoll() {
    when(pollRepository.save(any(Poll.class))).thenReturn(poll);
    when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    when(userRepository.findByEmail(user2.getEmail())).thenReturn(java.util.Optional.of(user2));

    createOrUpdatePollRequest.setEmails(Collections.singletonList("account2"));
    createOrUpdatePollRequest.getPoll().setQuestion("new question");
    createOrUpdatePollRequest.getPoll().setVisibilityType(PollVisibilityType.PRIVATE);
    createOrUpdatePollRequest.setPoll(poll);

    pollService.updatePoll(poll.getId(), createOrUpdatePollRequest, user.getId());

    Assertions.assertEquals("new question", poll.getQuestion());
    Assertions.assertEquals(1, poll.getAllowedVoters().size());

    verify(userRepository, times(1)).findByEmail(anyString());
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
    User pollOwner = new User().admin(true);
    pollOwner.setId(UUID.randomUUID());

    poll.setOwnerAndAddThisPollToOwner(pollOwner);

    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    when(userRepository.findById(pollOwner.getId())).thenReturn(java.util.Optional.of(pollOwner));
    when(voteRepository.findByPoll(poll)).thenReturn(Collections.singletonList(vote));

    pollService.deletePoll(poll.getId(), pollOwner.getId());

    verify(pollRepository, times(1)).delete(poll);
  }

  @Test
  public void shouldNotDeletePollWhenUserIsNotOwnerOrAdminTest() {
    User pollOwner = new User();
    pollOwner.setId(UUID.randomUUID());
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
                new Poll().visibilityType(PollVisibilityType.PUBLIC).pollOwner(user),
                new Poll().visibilityType(PollVisibilityType.PUBLIC).pollOwner(user),
                new Poll().visibilityType(PollVisibilityType.PUBLIC).pollOwner(user)));
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
    VotesResponse votes = pollService.getNumberOfVotes(poll.getId(), user.getId());
    Assertions.assertEquals(1, votes.getNo());
    Assertions.assertEquals(3, votes.getYes());
  }

  @Test
  public void shouldNotGetPollResultWhenNotAllowedTest() {
    poll.setVisibilityType(PollVisibilityType.PRIVATE);
    when(userRepository.findById(any(UUID.class))).thenReturn(java.util.Optional.empty());
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));

    Assertions.assertThrows(
        BadCredentialsException.class,
        () -> pollService.getNumberOfVotes(poll.getId(), UUID.randomUUID()));
  }

  @Test
  public void shouldGetPollTest() {
    poll.setVisibilityType(PollVisibilityType.PRIVATE);
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));

    PollResponse pollResponse = pollService.getPoll(poll.getId(), user.getId());

    Assertions.assertEquals(poll.getId(), pollResponse.getId());
  }

  @Test
  public void shouldNotGetPollWhenNotAllowedTest() {
    poll.setVisibilityType(PollVisibilityType.PRIVATE);
    when(userRepository.findById(user2.getId())).thenReturn(java.util.Optional.of(user2));
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));

    Assertions.assertThrows(
        BadCredentialsException.class, () -> pollService.getPoll(poll.getId(), user2.getId()));
  }

  @Test
  public void shouldActivateAPollTest() {
    poll.setPollDuration(10000000);
    when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
    when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));
    pollService.activatePoll(poll.getId(), user.getId());
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
  public void shouldGiveFinishedAndPublicPollsTest() {
    this.poll.setStartTime(Date.from(Instant.now()));
    this.poll.setPollDuration(0);
    when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
        .thenReturn(Collections.singletonList(this.poll));
    Assertions.assertEquals(1, pollService.getAllFinishedPublicPolls().size());
  }

  @Test
  public void shouldNotBeFinishedWhenNotActivated() {
    this.poll.setStartTime(null);
    this.poll.setPollDuration(1);
    when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
        .thenReturn(Collections.singletonList(this.poll));
    Assertions.assertEquals(0, pollService.getAllFinishedPublicPolls().size());
  }
}
