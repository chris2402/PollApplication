package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.exceptions.AlreadyVotedException;
import no.hvl.dat250.h2020.group5.exceptions.InvalidTimeException;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteService {

  final PollRepository pollRepository;

  final VoterRepository voterRepository;

  final VoteRepository voteRepository;

  final UserRepository userRepository;

  final StringToAnswerType stringToAnswerType = new StringToAnswerType();

  public VoteService(
      PollRepository pollRepository,
      VoterRepository voterRepository,
      VoteRepository voteRepository,
      UserRepository userRepository) {
    this.pollRepository = pollRepository;
    this.voterRepository = voterRepository;
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
  }

  public Vote vote(Long pollId, Long userId, CastVoteRequest castVoteRequest) {
    if (castVoteRequest.getVote() == null) {
      throw new IllegalArgumentException("Vote must contain an answer");
    }

    Optional<Poll> p = pollRepository.findById(pollId);
    Optional<Voter> u = voterRepository.findById(userId);
    AnswerType answer = stringToAnswerType.convert(castVoteRequest.getVote());

    if (answer == null){
      throw new IllegalArgumentException("Vote has no valid answer");
    }

    if (p.isEmpty()
        || u.isEmpty()) {
      throw new NotFoundException("Poll or voter not found");
    }

    if (p.get().getStartTime() == null || checkVoteDateTime(p)){
      throw new InvalidTimeException("The poll has expired or is not activated");
    }

    Optional<Vote> savedVote = voteRepository.findByVoterAndPoll(u.get(), p.get());
    if (savedVote.isPresent()){
      throw new AlreadyVotedException("You have already voted on this poll");
    }

    Vote v = new Vote();
    v.setPollAndAddThisVoteToPoll(p.get());
    v.setVoterAndAddThisVoteToVoter(u.get());
    v.setAnswer(answer);

    return voteRepository.save(v);
  }

  public Vote findVote(Long pollId, Long userId) {
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

  private Boolean checkVoteDateTime(Optional<Poll> p) {
    Instant startTime = p.get().getStartTime().toInstant();
    Instant startTimePlusDuration = startTime.plusSeconds(p.get().getPollDuration());
    return Instant.now().isAfter(startTimePlusDuration);
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

    if (poll.isPresent()) {
      List<Vote> votes = voteRequestFromDevice.getVotes();
      voteRepository.saveAll(votes);
      return voteRepository.saveAll(
          votes.stream()
              .peek(vote -> vote.setPollAndAddThisVoteToPoll(poll.get()))
              .collect(Collectors.toList()));
    }
    return null;
  }
}
