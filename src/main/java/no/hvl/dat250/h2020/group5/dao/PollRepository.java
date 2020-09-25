package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//TODO: Edit poll
public interface PollRepository extends JpaRepository<Poll, String> {
    List<Poll> findAllByVisibilityType(PollVisibilityType pvt);
    List<Poll> findAllByPollOwner(Voter owner);
    List<Poll> findAllByPollOwnerEquals(User owner);

    Optional<Poll> findById(long pollId);
}
