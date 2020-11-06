package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.exceptions.AlreadyVotedException;
import no.hvl.dat250.h2020.group5.exceptions.InvalidTimeException;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.*;
import no.hvl.dat250.h2020.group5.requests.VoteRequest;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VoteService {

  final PollRepository pollRepository;

  final VoterRepository voterRepository;

  final VoteRepository voteRepository;

  final UserRepository userRepository;

  final DeviceRepository deviceRepository;

  final ExtractFromAuth extractFromAuth;

  final StringToAnswerType stringToAnswerType = new StringToAnswerType();

  public VoteService(
      PollRepository pollRepository,
      VoterRepository voterRepository,
      VoteRepository voteRepository,
      UserRepository userRepository,
      DeviceRepository deviceRepository,
      ExtractFromAuth extractFromAuth) {
    this.pollRepository = pollRepository;
    this.voterRepository = voterRepository;
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
    this.deviceRepository = deviceRepository;
    this.extractFromAuth = extractFromAuth;
  }

  public Vote vote(Long pollId, UUID userId, VoteRequest voteRequest) {
    if (voteRequest.getVote() == null) {
      throw new IllegalArgumentException("Vote must contain an answer");
    }

    Optional<Poll> p = pollRepository.findById(pollId);
    Optional<Voter> u = voterRepository.findById(userId);
    AnswerType answer = stringToAnswerType.convert(voteRequest.getVote());

    checkVote(p, u, answer);

    Vote v = new Vote();
    v.setPollAndAddThisVoteToPoll(p.get());
    v.setVoterAndAddThisVoteToVoter(u.get());
    v.setAnswer(answer);

    return voteRepository.save(v);
  }

  public Vote findVote(Long pollId, UUID userId) {
    Optional<Voter> voter = voterRepository.findById(userId);
    Optional<Poll> poll = pollRepository.findById(pollId);

    if (voter.isEmpty() || poll.isEmpty()) {
      return null;
    }

    Optional<Vote> vote = voteRepository.findByVoterAndPoll(voter.get(), poll.get());

    if (vote.isEmpty()) {
      return null;
    }

    return vote.get();
  }

  /**
   * Saves all the votes to the given poll in the request from the device. Needs to save the votes
   * to repository before adding setting the poll because the vote needs an ID.
   *
   * @param voteRequestFromDevice
   * @return the list of votes saved from device
   */
  public List<Vote> saveVotesFromDevice(Long pollId, VoteRequestFromDevice voteRequestFromDevice) {
    Optional<Poll> poll = pollRepository.findById(pollId);
    if (poll.isEmpty()) {
      throw new NotFoundException("Poll not found");
    }
    Optional<VotingDevice> votingDevice = deviceRepository.findById(voteRequestFromDevice.getId());
    if (votingDevice.isEmpty()) {
      throw new NotFoundException("Device not found");
    }

    if (!deviceAllowed(poll.get(), voteRequestFromDevice.getId())) {
      throw new BadCredentialsException("You are not allowed to vote on this poll");
    }
    List<Vote> votes = voteRequestFromDevice.getVotes();
    voteRepository.saveAll(votes);
    return voteRepository.saveAll(
        votes.stream()
            .peek(vote -> vote.setPollAndAddThisVoteToPoll(poll.get()))
            .collect(Collectors.toList()));
  }

  private void checkVote(Optional<Poll> p, Optional<Voter> v, AnswerType answer) {
    if (answer == null) {
      throw new IllegalArgumentException("Vote has no valid answer");
    }

    if (p.isEmpty() || v.isEmpty()) {
      throw new NotFoundException("Poll or voter not found");
    }

    if (p.get().getStartTime() == null || checkVoteDateTime(p)) {
      throw new InvalidTimeException("The poll has expired or is not activated");
    }

    Optional<Vote> savedVote = voteRepository.findByVoterAndPoll(v.get(), p.get());
    if (savedVote.isPresent()) {
      throw new AlreadyVotedException("You have already voted on this poll");
    }
  }

  private Boolean checkVoteDateTime(Optional<Poll> p) {
    Instant startTime = p.get().getStartTime().toInstant();
    Instant startTimePlusDuration = startTime.plusSeconds(p.get().getPollDuration());
    return Instant.now().isAfter(startTimePlusDuration);
  }

  protected boolean deviceAllowed(Poll poll, UUID voterId) {
    return poll.getPollOwner().getVotingDevices().stream()
        .anyMatch(device -> device.getId().equals(voterId));
  }
}
