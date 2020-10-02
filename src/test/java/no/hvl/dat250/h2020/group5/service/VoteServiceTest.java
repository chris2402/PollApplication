package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class VoteServiceTest {

  @InjectMocks VoteService voteService;

  @Mock VoterRepository voterRepository;

  @Mock VoteRepository voteRepository;

  @Mock PollRepository pollRepository;

  private Poll poll;
  private Voter voter;
  private Voter voter2;
  private Vote vote;
  private CastVoteRequest castVoteRequest;

  @BeforeEach
  public void setUp() {
    poll = new Poll();
    poll.setId(1L);
    poll.setStartTime(new Date());
    poll.setPollDuration(100);

    voter = new User();
    voter.setId(2L);
    voter2 = new User();
    voter2.setId(5L);

    vote = new Vote();
    vote.setVoter(voter);
    vote.setPoll(poll);

    castVoteRequest = new CastVoteRequest();
    castVoteRequest.setPollId(poll.getId());
    castVoteRequest.setUserId(voter.getId());
    castVoteRequest.setVote("YES");

    when(pollRepository.findById(1L)).thenReturn(Optional.ofNullable(poll));
    when(pollRepository.findById(3L)).thenReturn(Optional.empty());
    when(voterRepository.findById(2L)).thenReturn(Optional.ofNullable(voter));
    when(voterRepository.findById(4L)).thenReturn(Optional.empty());

    when(voteRepository.save(any(Vote.class))).thenReturn(vote);
    when(voteRepository.findByVoterAndPoll(voter, poll)).thenReturn(Optional.ofNullable(vote));
    when(voteRepository.findByVoterAndPoll(voter2, poll)).thenReturn(Optional.empty());
  }

  @Test
  public void shouldNotCastVoteWhenPollHaveNotStartedTest() {
    poll.setStartTime(null);
    Assertions.assertNull(voteService.vote(castVoteRequest));
  }

  @Test
  public void shouldCastVoteWhenPollIsOnGoingAndVoteIsValidTest() {
    Assertions.assertEquals(vote, voteService.vote(castVoteRequest));
    castVoteRequest.setVote("NO");
    Assertions.assertEquals(vote, voteService.vote(castVoteRequest));
  }

  @Test
  public void shouldNotCastVoteWhenPollHaveEndedTest() {
    poll.setStartTime(new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime());
    Assertions.assertNull(voteService.vote(castVoteRequest));
  }

  @Test
  public void shouldNotCastVoteWithOutAnswerTest() {
    castVoteRequest.setVote("");
    Assertions.assertNull(voteService.vote(castVoteRequest));
  }

  @Test
  public void shouldNotCastVoteWhenAnswerIsNotValidTest() {
    castVoteRequest.setVote("this is not valid");
    Assertions.assertNull(voteService.vote(castVoteRequest));
  }

  @Test
  public void shouldNotCastVoteWithOutPollIdOrUserIdOrAnswerTest() {
    castVoteRequest.setPollId(null);
    Assertions.assertNull(voteService.vote(castVoteRequest));

    castVoteRequest.setPollId(poll.getId());
    castVoteRequest.setUserId(null);
    Assertions.assertNull(voteService.vote(castVoteRequest));

    castVoteRequest.setUserId(voter.getId());
    castVoteRequest.setVote(null);
    Assertions.assertNull(voteService.vote(castVoteRequest));
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
    Assertions.assertNull(voteService.findVote(poll.getId(), 4L));
    Assertions.assertNull(voteService.findVote(3L, voter.getId()));
  }

  @Test
  public void shouldRegisterOneYesVoteAndNoZeroVoteFromDeviceTest() {
    VoteRequestFromDevice voteRequestFromDevice = new VoteRequestFromDevice(1, 0);
    Assertions.assertEquals(1, voteService.saveVotesFromDevice(voteRequestFromDevice).size());
  }
}
