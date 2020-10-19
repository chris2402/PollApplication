package no.hvl.dat250.h2020.group5.repositories;

import no.hvl.dat250.h2020.group5.entities.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {
  Optional<Voter> findByUsername(String username);
}
