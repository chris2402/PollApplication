package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
//TODO: Edit poll
public interface PollRepository extends CrudRepository<Poll, String> {

    Poll createPoll(String name, String question, String userId, Integer duration, boolean isPublic);
    boolean deletePoll(String pollId);

    List<Poll> getAllPublicPolls();
    List<Poll> getPollsByUserId(String userId);
    Poll getPoll(String pollId);

    boolean changePollStatus(String pollId, boolean status);
}
