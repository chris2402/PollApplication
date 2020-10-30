package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.responses.VoterResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoterService {

  final VoterRepository voterRepository;

  public VoterService(VoterRepository voterRepository) {
    this.voterRepository = voterRepository;
  }

  public VoterResponse getVoter(Long userId) {
    Optional<Voter> voter = voterRepository.findById(userId);
    if (voter.isEmpty()) {
      return null;
    }
    return new VoterResponse(voter.get());
  }
}
