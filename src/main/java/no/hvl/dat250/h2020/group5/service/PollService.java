package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollDAO;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class PollService implements PollDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Poll createPoll(String name, String question, String alternativeA, String alternativeB, String userId) {
        Poll poll = new Poll();

        User pollOwner = em.find(User.class, userId);

        poll.setAlternativeA(alternativeA);
        poll.setAlternativeB(alternativeB);
        poll.setName(name);
        poll.setQuestion(question);
        poll.setPollOwner(pollOwner);

        em.persist(poll);

        return poll;
    }

    @Override
    public Boolean deletePoll(String pollId) {
        return null;
    }

    @Override
    public List<Poll> getAllPublicPolls() {
        return null;
    }

    @Override
    public List<Poll> getOwnPolls(String userId) {
        return null;
    }

    @Override
    public Poll getPoll(String pollId) {
        return em.find(Poll.class, pollId);
    }

    @Override
    public Boolean setInactive(String pollId) {
        return null;
    }
}
