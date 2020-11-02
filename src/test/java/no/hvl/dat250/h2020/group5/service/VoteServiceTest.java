package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.exceptions.AlreadyVotedException;
import no.hvl.dat250.h2020.group5.exceptions.InvalidTimeException;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.requests.VoteRequest;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class VoteServiceTest {

  @InjectMocks VoteService voteService;

  @Mock VoterRepository voterRepository;

  @Mock VoteRepository voteRepository;

  @Mock PollRepository pollRepository;

  @Mock DeviceRepository deviceRepository;

  private Poll poll;
  private Voter voter;
  private Voter voter2;
  private Vote vote;
  private VoteRequest voteRequest;
  private Vote yesVote;
  private Vote noVote;
  private VotingDevice device;

  @BeforeEach
  public void setUp() {
    poll = new Poll().startTime(new Date()).pollDuration(100);
    poll.setId(1L);

    voter = new User();
    voter.setId(UUID.randomUUID());
    voter2 = new User();
    voter2.setId(UUID.randomUUID());

    vote = new Vote();
    vote.setVoterAndAddThisVoteToVoter(voter);
    vote.setPollAndAddThisVoteToPoll(poll);

    voteRequest = new VoteRequest();
    voteRequest.setVote("YES");

    yesVote = new Vote().answer(AnswerType.YES);
    noVote = new Vote().answer(AnswerType.NO);

    when(pollRepository.findById(1L)).thenReturn(Optional.ofNullable(poll));
    when(pollRepository.findById(3L)).thenReturn(Optional.empty());

    when(voterRepository.findById(voter.getId())).thenReturn(Optional.ofNullable(voter));
    when(voterRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
    when(voterRepository.findById(voter2.getId())).thenReturn(Optional.ofNullable(voter2));

    when(voteRepository.save(any(Vote.class))).thenReturn(vote);
    when(voteRepository.findByVoterAndPoll(voter, poll)).thenReturn(Optional.ofNullable(vote));
    when(voteRepository.findByVoterAndPoll(voter2, poll)).thenReturn(Optional.empty());

    device = new VotingDevice().displayName("Test");
    when(deviceRepository.findById(device.getId())).thenReturn(Optional.ofNullable(device));
  }

  @Test
  public void shouldNotCastVoteWhenPollHaveNotStartedTest() {
    poll.setStartTime(null);
    Assertions.assertThrows(
        InvalidTimeException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldCastVoteWhenPollIsOnGoingAndVoteIsValidTest() {
    Assertions.assertEquals(vote, voteService.vote(poll.getId(), voter2.getId(), voteRequest));
    voteRequest.setVote("NO");
    Assertions.assertEquals(vote, voteService.vote(poll.getId(), voter2.getId(), voteRequest));
  }

  @Test
  public void shouldNotCastVoteWhenPollHaveEndedTest() {
    poll.setStartTime(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
    Assertions.assertThrows(
        InvalidTimeException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldNotCastVoteWhenAlreadyVoted() {
    Assertions.assertThrows(
        AlreadyVotedException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldNotCastVoteWithOutAnswerTest() {
    voteRequest.setVote(null);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldNotCastVoteWhenAnswerIsNotValidTest() {
    voteRequest.setVote("this is not valid");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldNotCastVoteWithOutPollIdOrUserIdOrAnswerTest() {
    // castVoteRequest.setPollId(null);
    Assertions.assertThrows(
        NotFoundException.class, () -> voteService.vote(null, voter.getId(), voteRequest));

    // castVoteRequest.setPollId(poll.getId());
    // castVoteRequest.setUserId(null);
    Assertions.assertThrows(
        NotFoundException.class, () -> voteService.vote(poll.getId(), null, voteRequest));

    // castVoteRequest.setUserId(voter.getId());
    voteRequest.setVote(null);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> voteService.vote(poll.getId(), voter.getId(), voteRequest));
  }

  @Test
  public void shouldFindVoteWhenItExistsTest() {
    Assertions.assertEquals(vote, voteService.findVote(poll.getId(), voter.getId()));
  }

  @Test
  public void shouldNotFindVoteWhenItDoesNotExistsTest() {
    Assertions.assertNull(voteService.findVote(poll.getId(), voter2.getId()));
  }

  @Test
  public void shouldNotFindVoteWhenUserOrPollDoesNotExistsTest() {
    Assertions.assertNull(voteService.findVote(poll.getId(), UUID.randomUUID()));
    Assertions.assertNull(voteService.findVote(3L, voter.getId()));
  }

  @Test
  public void shouldRegisterOneYesVoteAndZeroNoVoteFromDeviceTest() {
    VoteRequestFromDevice voteRequestFromDevice = new VoteRequestFromDevice(device.getId(), 1, 0);
    List<Vote> votes = Collections.singletonList(new Vote().answer(AnswerType.YES));
    when(voteRepository.saveAll(votes)).thenReturn(votes);

    List<Vote> savedVotes = voteService.saveVotesFromDevice(poll.getId(), voteRequestFromDevice);

    Assertions.assertEquals(1, savedVotes.size());
    Assertions.assertEquals(AnswerType.YES, savedVotes.get(0).getAnswer());
  }

  @Test
  public void shouldRegisterTwoYesAndThreeNoVoteFromDeviceTest() {
    VotingDevice device = new VotingDevice().displayName("uuid");
    VoteRequestFromDevice voteRequestFromDevice = new VoteRequestFromDevice(device.getId(), 2, 3);
    List<Vote> votes = Arrays.asList(yesVote, yesVote, noVote, noVote, noVote);

    when(voteRepository.saveAll(votes)).thenReturn(votes);

    List<Vote> savedVotes = voteService.saveVotesFromDevice(poll.getId(), voteRequestFromDevice);
    Assertions.assertEquals(5, savedVotes.size());
    int yesVotes =
        (int) savedVotes.stream().filter(vote -> vote.getAnswer().equals(AnswerType.YES)).count();
    int noVotes =
        (int) savedVotes.stream().filter(vote -> vote.getAnswer().equals(AnswerType.NO)).count();
    Assertions.assertEquals(2, yesVotes);
    Assertions.assertEquals(3, noVotes);
  }

  @Test
  public void shouldSaveAllVotesFromDeviceTest() {
    VoteRequestFromDevice voteRequestFromDevice = new VoteRequestFromDevice(device.getId(), 4, 3);
    List<Vote> votes = Arrays.asList(yesVote, yesVote, yesVote, yesVote, noVote, noVote, noVote);
    voteService.saveVotesFromDevice(poll.getId(), voteRequestFromDevice);
    verify(voteRepository, times(2)).saveAll(votes);
  }
}
