package no.hvl.dat250.h2020.group5.repositories;

import no.hvl.dat250.h2020.group5.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByDisplayName(String displayName);

  Optional<User> findByEmail(String email);
}
