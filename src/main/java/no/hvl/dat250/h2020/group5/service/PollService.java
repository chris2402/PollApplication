package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.exceptions.InvalidTimeException;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.CreateOrUpdatePollRequest;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
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

  @Transactional
  public PollResponse createPoll(CreateOrUpdatePollRequest createOrUpdatePollRequest, UUID userId) {
    Optional<User> foundUser = userRepository.findById(userId);
    if (foundUser.isEmpty()) {
      throw new NotFoundException("User not found when creating poll");
    }
    Poll poll = createOrUpdatePollRequest.getPoll();
    poll.setOwnerAndAddThisPollToOwner(foundUser.get());

    addEmails(poll, createOrUpdatePollRequest);

    pollRepository.save(poll);
    return new PollResponse(poll);
  }

  public PollResponse updatePoll(Long pollId, CreateOrUpdatePollRequest request, UUID userId) {
    Optional<Poll> foundPoll = pollRepository.findById(pollId);

    if (foundPoll.isEmpty()) {
      throw new NotFoundException("Cannot update poll. Poll not found");
    }
    Poll poll = foundPoll.get();
    Poll updatedPoll = request.getPoll();

    if (poll.getStartTime() != null) {
      throw new InvalidTimeException("Cannot edit a poll that has started");
    }

    if (!isOwnerOrAdmin(poll, userId)) {
      throw new BadCredentialsException("Not allowed");
    }

    poll.question(updatedPoll.getQuestion())
        .name(updatedPoll.getName())
        .visibilityType(updatedPoll.getVisibilityType())
        .pollDuration(updatedPoll.getPollDuration());
    addEmails(poll, request);

    return new PollResponse(pollRepository.save(poll));
  }

  public boolean deletePoll(Long pollId, UUID userId) {
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

  @Transactional
  public List<PollResponse> getUserPollsAsOwner(UUID userId) {
    Optional<User> user = userRepository.findById(userId);
    return user.map(
            value ->
                pollRepository.findAllByPollOwner(user.get()).stream()
                    .map(PollResponse::new)
                    .collect(Collectors.toList()))
        .orElse(null);
  }

  public PollResponse getPoll(Long pollId, UUID userId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      throw new NotFoundException("Poll not found");
    }
    if (poll.get().getVisibilityType().equals(PollVisibilityType.PUBLIC)) {
      return new PollResponse(poll.get());
    }

    if (userId != null
        && (isOwnerOrAdmin(poll.get(), userId) || allowedToVote(poll.get(), userId))) {
      return new PollResponse(poll.get());
    }
    throw new BadCredentialsException("You have no access to this poll");
  }

  public Boolean activatePoll(Long pollId, UUID userId) {

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
                    + "&pollId="
                    + pollId
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
  public VotesResponse getNumberOfVotesAsAdmin(Long pollId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      throw new NotFoundException("Poll not found");
    }
    return countVotes(poll.get());
  }

  @Transactional
  public VotesResponse getNumberOfVotes(Long pollId, UUID userId) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      throw new NotFoundException("Poll not found");
    }
    if (poll.get().getVisibilityType().equals(PollVisibilityType.PRIVATE)
        && !(isOwnerOrAdmin(poll.get(), userId) || allowedToVote(poll.get(), userId))) {
      throw new BadCredentialsException("You are not allowed to view this poll");
    }

    return countVotes(poll.get());
  }

  private VotesResponse countVotes(Poll poll) {
    VotesResponse votesResponse = new VotesResponse();
    int yes = 0;
    int no = 0;

    for (Vote vote : poll.getVotes()) {
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

  private boolean allowedToVote(Poll poll, UUID userId) {
    return poll.getAllowedVoters().stream().anyMatch(user -> user.getId().equals(userId));
  }

  private boolean isOwnerOrAdmin(Poll poll, UUID userId) {
    Optional<User> maybeUser = userRepository.findById(userId);
    return maybeUser
        .filter(user -> user.getId().equals(poll.getPollOwner().getId()) || user.getIsAdmin())
        .isPresent();
  }

  private void addEmails(Poll poll, CreateOrUpdatePollRequest request) {
    poll.getAllowedVoters().clear();
    request
        .getEmails()
        .forEach(
            email -> {
              Optional<User> user = userRepository.findByEmail(email);
              user.ifPresent(value -> poll.getAllowedVoters().add(value));
            });
  }
}
