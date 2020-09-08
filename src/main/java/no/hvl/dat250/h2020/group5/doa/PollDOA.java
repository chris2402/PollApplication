package no.hvl.dat250.h2020.group5.doa;

import no.hvl.dat250.h2020.group5.entities.Poll;

import java.util.List;

public interface PollDOA {
    Boolean createPoll(String name, String question, String alternativeA, String alternativeB);
    Boolean deletePoll(String pollId);

    List<Poll> getAllPublicPolls();
    List<Poll> getOwnPolls(String userId);
    Poll getPoll(String pollId);

    Boolean setInactive(String pollId);
}
