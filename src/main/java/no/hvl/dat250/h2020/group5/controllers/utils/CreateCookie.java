package no.hvl.dat250.h2020.group5.controllers.utils;

import no.hvl.dat250.h2020.group5.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class CreateCookie {
  final AuthenticationManager authenticationManager;
  final JwtUtils jwtUtils;

  public CreateCookie(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  public String signIn(String username, String password, HttpServletResponse response) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    Cookie cookie = new Cookie("auth", jwt);
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(Integer.MAX_VALUE);
    cookie.setPath("/");
    response.addCookie(cookie);

    return jwt;
  }
}
