package no.hvl.dat250.h2020.group5.security.services;

import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.repositories.AccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  final AccountRepository accountRepository;

  public UserDetailsServiceImpl(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  @Transactional
  public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Account> account = accountRepository.findByEmail(email);

    if (account.isEmpty()) {
      throw new UsernameNotFoundException("User Not Found with email: " + email);
    }

    return UserDetailsImpl.build(account.get());
  }

  @Transactional
  public UserDetailsImpl loadById(Long id) throws UsernameNotFoundException {
    Optional<Account> account = accountRepository.findById(id);

    if (account.isEmpty()) {
      throw new UsernameNotFoundException("User Not Found with username: " + id);
    }

    return UserDetailsImpl.build(account.get());
  }
}
