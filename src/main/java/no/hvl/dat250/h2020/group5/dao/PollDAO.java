package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Poll;

import java.util.List;
//TODO: Edit poll
public interface PollDAO {
    Poll createPoll(String name, String question, String userId, Integer duration);
    boolean deletePoll(String pollId);

    List<Poll> getAllPublicPolls();
    List<Poll> getOwnPolls(String userId);
    Poll getPoll(String pollId);

    boolean changePollStatus(String pollId, boolean status);
}
