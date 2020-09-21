package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.dao.VoterRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
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

    public boolean vote(String pollId, Long userId, String vote) {
        Optional<Poll> p = pollRepository.findById(pollId);
        Optional<Voter> u = voterRepository.findById(userId);
        AnswerType answer = setAnswer(vote);

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
    public boolean changeVote(String pollId, Long userId, String vote) {
        Optional<Vote> foundVote = voteRepository.findByVoterAndPollId(userId, pollId);
        if(setAnswer(vote) != null && foundVote.isPresent()) {
            foundVote.get().setAnswer(setAnswer(vote));
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

    private AnswerType setAnswer(String answer){
        switch(answer.toLowerCase()){
            case "yes":
                return AnswerType.YES;
            case "no":
                return AnswerType.NO;
            default:
                return null;
        }
    }

    private Vote findVote(String pollId, Long userId){
        Optional<Vote> vote = voteRepository.findByVoterAndPollId(userId,pollId);
        if(vote.isEmpty()){
            return null;
        }
        return vote.get();
    }

}
