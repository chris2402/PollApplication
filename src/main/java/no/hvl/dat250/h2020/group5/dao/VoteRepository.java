package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoterAndPollId(String userId, String pollId);
    List<Vote> findByVoter(String userId);
}
