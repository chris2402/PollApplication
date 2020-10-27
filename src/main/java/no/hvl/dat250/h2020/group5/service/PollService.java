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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PollService {

  private static final Logger logger = LoggerFactory.getLogger(PollService.class);

  final PollRepository pollRepository;

  final UserRepository userRepository;

  final VoteRepository voteRepository;

  private final WebClient webClient =
      WebClient.create("https://dweet.io/dweet/for/poll-application-group5");

  public PollService(
      PollRepository pollRepository, UserRepository userRepository, VoteRepository voteRepository) {
    this.pollRepository = pollRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
  }

  public PollResponse createPoll(Poll poll, Long userId) {
    Optional<User> foundUser = userRepository.findById(userId);
    if (foundUser.isPresent()) {
      User user = foundUser.get();
      poll.setOwnerAndAddThisPollToOwner(user);
      pollRepository.save(poll);
      return new PollResponse(poll);
    }
    return null;
  }

  public boolean deletePoll(Long pollId, Long userId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty() || !isOwnerOrAdmin(poll.get(), userId)) {
      return false;
    }

    for (Vote vote : voteRepository.findByPoll(poll.get())) {
      vote.setVoterAndAddThisVoteToVoter(null);
      vote.setPollAndAddThisVoteToPoll(null);
      voteRepository.save(vote);
      voteRepository.delete(vote);
    }

    User user = poll.get().getPollOwner();
    user.detachPoll(poll.get());

    pollRepository.delete(poll.get());
    return true;
  }

  public List<PollResponse> getAllPublicPolls() {
    return pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC).stream()
        .map(PollResponse::new)
        .collect(Collectors.toList());
  }

  public List<PollResponse> getAllPolls() {
    return pollRepository.findAll().stream().map(PollResponse::new).collect(Collectors.toList());
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

  public Boolean activatePoll(Long pollId, Long userId) {

    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty() || !isOwnerOrAdmin(poll.get(), userId)) {
      return false;
    }

    logger.info("Sending tweet when activating poll");
    Mono<String> response =
        webClient
            .get()
            .uri(
                "?pollName="
                    + poll.get().getName()
                    + "&question="
                    + poll.get().getQuestion()
                    + "&status=started")
            .retrieve()
            .bodyToMono(String.class);
    logger.info(response.block());

    poll.get().setStartTime(new Date());
    pollRepository.save(poll.get());
    return true;
  }

  public List<Poll> getAllFinishedPublicPolls() {
    List<Poll> finishedPolls = new ArrayList<>();
    List<Poll> publicPolls = pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC);
    for (Poll poll : publicPolls) {
      if (poll.getStartTime() != null && !isActivated(poll.getId())) {
        finishedPolls.add(poll);
      }
    }
    return finishedPolls;
  }

  public boolean isActivated(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      return false;
    }

    if (poll.get().getStartTime() == null) {
      return false;
    }

    Instant startTime = poll.get().getStartTime().toInstant();
    Instant startTimePlusDuration = startTime.plusSeconds(poll.get().getPollDuration());

    return Instant.now().isBefore(startTimePlusDuration);
  }

  @Transactional
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
