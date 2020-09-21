package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Vote;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoteRepository extends CrudRepository<Vote, Long> {
    Optional<Vote> findByUserIdAmdPollId(String userId, String pollId);
    boolean vote(String pollId, String userId, String vote);
    boolean changeVote(String pollId, String userId, String vote);
}
