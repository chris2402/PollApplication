package no.hvl.dat250.h2020.group5.repositories;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
  List<Poll> findAllByVisibilityType(PollVisibilityType pvt);

  List<Poll> findAllByPollOwner(User owner);

  List<Poll> findAllByPollOwnerEquals(User owner);

  Optional<Poll> findById(Long pollId);
}
