package no.hvl.dat250.h2020.group5.security.services;

import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  final VoterRepository voterRepository;

  public UserDetailsServiceImpl(VoterRepository voterRepository) {
    this.voterRepository = voterRepository;
  }

  @Override
  @Transactional
  public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Voter> voter = voterRepository.findByUsername(username);

    if (voter.isEmpty()) {
      throw new UsernameNotFoundException("User Not Found with username: " + username);
    }

    return UserDetailsImpl.build(voter.get());
  }
}
