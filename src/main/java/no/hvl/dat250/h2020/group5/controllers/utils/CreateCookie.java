package no.hvl.dat250.h2020.group5.controllers.utils;

import no.hvl.dat250.h2020.group5.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreateCookie {
  final AuthenticationManager authenticationManager;
  final JwtUtils jwtUtils;

  @Value("${poll.app.test.environment}")
  private Boolean isTest;

  @Value("${poll.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public CreateCookie(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  public List<String> signIn(String username, String password, HttpServletResponse response) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    response.setHeader(
        "Set-Cookie",
        "auth="
            + jwt
            + ";path=/;"
            + (isTest ? "" : "SameSite=None;")
            + (isTest ? "" : "Secure;")
            + ";HttpOnly;"
            + " Max-Age="
            + Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS));

    List<String> roles =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return roles;
  }
}
