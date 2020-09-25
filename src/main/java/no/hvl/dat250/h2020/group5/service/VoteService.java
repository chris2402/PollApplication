package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.converters.StringToAnswerType;
import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.dao.VoterRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;

import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class VoteService {

    final
    PollRepository pollRepository;

    final
    VoterRepository voterRepository;

    final
    VoteRepository voteRepository;

    final UserRepository userRepository;

    final
    StringToAnswerType stringToAnswerType = new StringToAnswerType();

    public VoteService(PollRepository pollRepository, VoterRepository voterRepository, VoteRepository voteRepository,
                       UserRepository userRepository) {
        this.pollRepository = pollRepository;
        this.voterRepository = voterRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
    }

    public Vote vote(CastVoteRequest castVoteRequest) {
        Optional<Poll> p = pollRepository.findById(castVoteRequest.getPollId());
        Optional<Voter> u = voterRepository.findById(castVoteRequest.getUserId());
        AnswerType answer = stringToAnswerType.convert(castVoteRequest.getVote());

        if (p.isEmpty() || u.isEmpty() || answer == null ){
            return null;
        }

        //Checking if the vote is cast before poll ended.
//        Instant startTime = p.get().getStartTime().toInstant();
//        Instant startTimePlusDuration = startTime.plusSeconds(p.get().getPollDuration());
//        if(Instant.now().isAfter(startTimePlusDuration)){
//            return false;
//        }

        Vote v = new Vote();
        v.setPoll(p.get());
        v.setVoter(u.get());
        v.setAnswer(answer);

        return voteRepository.save(v);
    }

    //Might be better to just use the vote function
    public boolean changeVote(Long id, String newAnswer) {
            Optional<Vote> foundVote = voteRepository.findById(id);
            AnswerType avt = stringToAnswerType.convert(newAnswer);
            if (foundVote.isPresent() && avt != null) {
                foundVote.get().setAnswer(avt);
                voteRepository.save(foundVote.get());
                return true;
            }
        return false;
    }

    public boolean deleteVote(Long id) {
            Optional<Vote> foundVote = voteRepository.findById(id);

            if (foundVote.isEmpty()) {
                return false;
            } else{
                voteRepository.delete(foundVote.get());
                return true;
            }
    }


    public Vote findVote(Long pollId, Long userId){
        Optional<Voter> foundVoter = voterRepository.findById(userId);
        Optional<Poll> foundPoll = pollRepository.findById(pollId);

        if(foundVoter.isPresent() && foundPoll.isPresent()) {
            Voter voter = foundVoter.get();
            Poll poll = foundPoll.get();

            Optional<Vote> vote = voteRepository.findByVoterAndPoll(voter,poll);

            if(vote.isEmpty()){
                return null;
            }
            return vote.get();
        }
        else{
            return null;
        }
    }
}
