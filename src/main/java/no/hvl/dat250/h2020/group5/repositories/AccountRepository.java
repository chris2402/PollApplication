package no.hvl.dat250.h2020.group5.repositories;

import no.hvl.dat250.h2020.group5.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByEmail(String email);
}
