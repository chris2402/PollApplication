package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.VoteDAO;
import no.hvl.dat250.h2020.group5.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//TODO: Change from user to voter during finds.

public class VoteService implements VoteDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    //TODO: Check if poll is active
    public boolean vote(String pollId, String userId, String vote) {
        Poll p = em.find(Poll.class, pollId);
        User u = em.find(User.class, userId);
        AnswerType answer = setAnswer(vote);

        if(p != null && u != null && answer != null){
            Vote v = new Vote();
            v.setPoll(p);
            v.setVoter(u);
            v.setAnswer(answer);

            em.persist(v);
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
            em.merge(foundVote);
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public boolean deleteVote(String pollId, String userId) {
        Vote foundVote = findVote(pollId, userId);

        if(foundVote == null){
            return false;
        }
        else{
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE from Vote v WHERE v.poll = :poll and v.voter = :voter");

            q.setParameter("poll", em.find(Poll.class, pollId));
            q.setParameter("voter", em.find(Voter.class, userId));

            int deleted = q.executeUpdate();
            em.getTransaction().commit();

            return deleted == 1;
        }
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
        User u = em.find(User.class, userId);

        Query q = em.createQuery("select v from Vote v where v.poll = :poll and v.voter = :voter");
        q.setParameter("poll", p);
        q.setParameter("voter", u);
        return (Vote) q.getResultList().get(0);
    }
}
