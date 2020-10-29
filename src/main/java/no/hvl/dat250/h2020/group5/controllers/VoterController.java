package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.responses.VoterResponse;
import no.hvl.dat250.h2020.group5.service.VoterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('GUEST') or hasAuthority('USER')")
@RestController
@RequestMapping("/voters")
public class VoterController {

  final VoterService voterService;
  private final ExtractIdFromAuth extractIdFromAuth;

  public VoterController(VoterService voterService, ExtractIdFromAuth extractIdFromAuth) {
    this.voterService = voterService;
    this.extractIdFromAuth = extractIdFromAuth;
  }

  @GetMapping("/me")
  public VoterResponse getMe(Authentication authentication) {
    VoterResponse voter = voterService.getVoter(extractIdFromAuth.getIdFromAuth(authentication));
    voter.setRoles(
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    return voter;
  }
}
