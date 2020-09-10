package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollDAO;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

//TODO: Edit poll
public class PollService implements PollDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Poll createPoll(String name, String question, String userId, Integer duration) {
        Poll poll = new Poll();

        User pollOwner = em.find(User.class, userId);

        poll.setName(name);
        poll.setQuestion(question);
        poll.setPollOwner(pollOwner);
        poll.setPollDuration(duration);

        em.persist(poll);
        Query q = em.createQuery("select p from Poll p where p.name = :pollName and p.question =:pollQuestion " +
                "and p.pollOwner = :pollOwner and p.pollDuration = :pollDuration");
        q.setParameter("pollName", name);
        q.setParameter("pollQuestion", question);
        q.setParameter("pollOwner", pollOwner);
        q.setParameter("pollDuration", duration);

        try{
            return (Poll) q.getResultList().get(0);
        }catch(ClassCastException e){
            return null;
        }
    }

    @Override
    //TODO: Check if user owns the poll or is an admin
    public boolean deletePoll(String pollId) {
        Poll pollToDelete = em.find(Poll.class, pollId);
        if(pollToDelete == null){
            return false;
        }
        else{
            deleteVotes(pollId);

            em.getTransaction().begin();
            Query q = em.createQuery("DELETE from Poll p WHERE p.id = :id");
            q.setParameter("id", pollId);
            int deleted = q.executeUpdate();
            em.getTransaction().commit();

            return deleted == 1;
        }
    }

    @Override
    public List<Poll> getAllPublicPolls() {
        Query q = em.createQuery("select p from Poll p where p.visibilityType = 1");
        List<Poll> polls = q.getResultList();
        return polls;
    }

    @Override
    public List<Poll> getOwnPolls(String userId) {
        User user = em.find(User.class, userId);

        Query q = em.createQuery("select p from Poll p where p.pollOwner = :owner");
        q.setParameter("owner", user);

        try{
            return (List<Poll>) q.getResultList();
        }catch(ClassCastException e){
            return null;
        }
    }

    @Override
    public Poll getPoll(String pollId) {
        return em.find(Poll.class, pollId);
    }



    @Override
    //TODO: Check if user owns the poll or is an admin
    public boolean changePollStatus(String pollId, boolean status) {
        Poll poll = em.find(Poll.class, pollId);
        if(poll == null){
            return false;
        }else{
            poll.setActive(true);
            em.merge(poll);
            return true;
        }
    }

    private void deleteVotes(String pollId){
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE from Poll p WHERE p.id = :id");
        q.setParameter("id", pollId);
        q.executeUpdate();
        em.getTransaction().commit();
    }
}
