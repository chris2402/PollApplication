package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  public Vote vote(CastVoteRequest castVoteRequest) {
    if (castVoteRequest.getPollId() == null
        || castVoteRequest.getUserId() == null
        || castVoteRequest.getVote() == null) {
      return null;
    }

    Optional<Poll> p = pollRepository.findById(castVoteRequest.getPollId());
    Optional<Voter> u = voterRepository.findById(castVoteRequest.getUserId());
    AnswerType answer = stringToAnswerType.convert(castVoteRequest.getVote());

    if (p.isEmpty()
        || p.get().getStartTime() == null
        || u.isEmpty()
        || answer == null
        || checkVoteDateTime(p)) {
      return null;
    }

    Vote v = new Vote();
    v.setPoll(p.get());
    v.setVoter(u.get());
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

  public List<Vote> saveVotesFromDevice(VoteRequestFromDevice voteRequestFromDevice) {
    List<Vote> votes = new ArrayList<>();
    for (int numberOfYesVotes = 0;
        numberOfYesVotes < voteRequestFromDevice.getNumberOfYes();
        numberOfYesVotes++) {
      votes.add(new Vote().answer(AnswerType.YES));
    }
    for (int numberOfNoVotes = 0;
        numberOfNoVotes < voteRequestFromDevice.getNumberOfNo();
        numberOfNoVotes++) {
      votes.add(new Vote().answer(AnswerType.NO));
    }
    return voteRepository.saveAll(votes);
  }
}
