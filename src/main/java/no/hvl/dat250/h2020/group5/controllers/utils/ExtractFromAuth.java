package no.hvl.dat250.h2020.group5.controllers.utils;

import no.hvl.dat250.h2020.group5.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExtractFromAuth {

  public UUID userId(Authentication authentication) {
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
    return principal.getId();
  }
}
