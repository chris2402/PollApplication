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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PollService {

  final PollRepository pollRepository;

  final UserRepository userRepository;

  final VoteRepository voteRepository;

  public PollService(
      PollRepository pollRepository, UserRepository userRepository, VoteRepository voteRepository) {
    this.pollRepository = pollRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
  }

  public Poll createPoll(Poll poll, Long userId) {
    Optional<User> foundUser = userRepository.findById(userId);
    if (foundUser.isPresent()) {
      User user = foundUser.get();
      poll.setPollOwner(user);
      return pollRepository.save(poll);
    } else {
      return null;
    }
  }

  public boolean deletePoll(Long pollId, Long userId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      return false;
    }
    if (isOwnerOrAdmin(poll.get(), userId)) {
      pollRepository.delete(poll.get());
      return true;
    }
    return false;
  }

  public List<PollResponse> getAllPublicPolls() {
    return pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC).stream()
        .map(PollResponse::new)
        .collect(Collectors.toList());
  }

  public List<PollResponse> getAllPolls(Long adminId) {
    Optional<User> maybeUser = userRepository.findById(adminId);
    if (maybeUser.isPresent() && maybeUser.get().getIsAdmin()) {
      return pollRepository.findAll().stream().map(PollResponse::new).collect(Collectors.toList());
    }
    return null;
  }

  public List<PollResponse> getUserPollsAsAdmin(Long userId, Long adminId) {
    Optional<User> maybeUser = userRepository.findById(adminId);
    if (maybeUser.isPresent() && maybeUser.get().getIsAdmin()) {
      return getUserPollsAsOwner(userId);
    }
    return null;
  }

  public List<PollResponse> getUserPollsAsOwner(Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isPresent()) {
      return pollRepository.findAllByPollOwner(user.get()).stream()
          .map(PollResponse::new)
          .collect(Collectors.toList());
    }
    return null;
  }

  public PollResponse getPoll(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    return poll.map(PollResponse::new).orElse(null);
  }

  public Boolean activatePoll(Long pollId) {

    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      return false;
    }

    poll.get().setStartTime(new Date());
    pollRepository.save(poll.get());
    return true;
  }

  public boolean isActivated(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      return false;
    }

    Instant startTime = poll.get().getStartTime().toInstant();
    Instant startTimePlusDuration = startTime.plusSeconds(poll.get().getPollDuration());

    return Instant.now().isBefore(startTimePlusDuration);
  }

  public VotesResponse getNumberOfVotes(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      return null;
    }

    VotesResponse votesResponse = new VotesResponse();
    int yes = 0;
    int no = 0;

    for (Vote vote : poll.get().getVotes()) {
      if ((vote.getAnswer().equals(AnswerType.YES))) {
        yes++;
      } else {
        no++;
      }
    }

    votesResponse.setNo(no);
    votesResponse.setYes(yes);
    return votesResponse;
  }

  private boolean isOwnerOrAdmin(Poll poll, Long userId) {
    Optional<User> maybeUser = userRepository.findById(userId);
    return maybeUser
        .filter(user -> user.getId().equals(poll.getPollOwner().getId()) || user.getIsAdmin())
        .isPresent();
  }
}
