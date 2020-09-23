package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.dao.VoterRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
public class VoteService {

    final
    PollRepository pollRepository;

    final
    VoterRepository voterRepository;

    final
    VoteRepository voteRepository;

    public VoteService(PollRepository pollRepository, VoterRepository voterRepository, VoteRepository voteRepository) {
        this.pollRepository = pollRepository;
        this.voterRepository = voterRepository;
        this.voteRepository = voteRepository;
    }

    public boolean vote(CastVoteRequest castVoteRequest) {
        Optional<Poll> p = pollRepository.findById(castVoteRequest.getPollId());
        Optional<Voter> u = voterRepository.findById(castVoteRequest.getUserId());
        AnswerType answer = setAnswer(castVoteRequest.getVote());

        if (p.isEmpty() || u.isEmpty() || answer == null ){
            return false;
        }

        //Checking if the vote is cast before poll ended.
        Instant startTime = p.get().getStartTime().toInstant();
        Instant startTimePlusDuration = startTime.plusSeconds(p.get().getPollDuration());
        if(Instant.now().isAfter(startTimePlusDuration)){
            return false;
        }

        Vote v = new Vote();
        v.setPoll(p.get());
        v.setVoter(u.get());
        v.setAnswer(answer);

        voteRepository.save(v);
        return true;
    }

    //Might be better to just use the vote function
    public boolean changeVote(CastVoteRequest castVoteRequest) {
        Optional<Vote> foundVote = voteRepository.findByVoterAndPollId(castVoteRequest.getUserId(), castVoteRequest.getPollId());
        if(setAnswer(castVoteRequest.getVote()) != null && foundVote.isPresent()) {
            foundVote.get().setAnswer(setAnswer(castVoteRequest.getVote()));
            voteRepository.save(foundVote.get());
            return true;
        }
        return false;
    }

    public boolean deleteVote(String pollId, Long userId) {
        Optional<Vote> foundVote = voteRepository.findByVoterAndPollId(userId, pollId);
        if(foundVote.isEmpty()){
            return false;
        }

        voteRepository.delete(foundVote.get());
        return true;
    }


    public Vote findVote(String pollId, Long userId){
        Optional<Vote> vote = voteRepository.findByVoterAndPollId(userId,pollId);
        if(vote.isEmpty()){
            return null;
        }
        return vote.get();
    }

    private AnswerType setAnswer(String answer){
        return switch (answer.toLowerCase()) {
            case "yes" -> AnswerType.YES;
            case "no" -> AnswerType.NO;
            default -> null;
        };
    }

}
