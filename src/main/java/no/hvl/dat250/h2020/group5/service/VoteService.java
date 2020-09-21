package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.dao.VoterRepository;
import no.hvl.dat250.h2020.group5.entities.*;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.Optional;

//TODO: Check if poll is active
@Service
public class VoteService {

    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoterRepository voterRepository;

    @Autowired
    VoteRepository voteRepository;

    public boolean vote(String pollId, String userId, String vote) {
        Optional<Poll> p = pollRepository.findById(pollId);
        Optional<Voter> u = voterRepository.findById(userId);
        AnswerType answer = setAnswer(vote);

        if(p.isPresent() && u.isPresent() && answer != null){
            Vote v = new Vote();
            v.setPoll(p.get());
            v.setVoter(u.get());
            v.setAnswer(answer);

            voteRepository.save(v);
            return true;
        }else{
            return false;
        }
    }

    @Override
    //Might be better to just use the vote function
    public boolean changeVote(String pollId, String userId, String vote) {
        Vote foundVote = findVote(pollId, userId);

        if(setAnswer(vote) != null && foundVote != null) {
            foundVote.setAnswer(setAnswer(vote));
            em.getTransaction().begin();
            em.merge(foundVote);
            em.getTransaction().commit();
            return true;
        }
        else{
            return false;
        }

    }

    public boolean deleteVote(String pollId, String userId) {
        Optional<Vote> foundVote = voteRepository.findByUserIdAmdPollId(userId, pollId);
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

    private Vote findVote(String pollId, String userId){
        Poll p = em.find(Poll.class, pollId);
        Voter u = em.find(Voter.class, userId);

        Query q = em.createQuery("select v from Vote v where v.poll = :poll and v.voter = :voter");
        q.setParameter("poll", p);
        q.setParameter("voter", u);
        return (Vote) q.getResultList().get(0);
    }

    public void setup(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }

}
