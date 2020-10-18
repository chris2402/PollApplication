package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.security.jwt.JwtUtils;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
  final AuthenticationManager authenticationManager;
  final JwtUtils jwtUtils;
  private final UserService userService;

  public AuthController(
      UserService userService,
      AuthenticationManager authenticationManager,
      PasswordEncoder encoder,
      JwtUtils jwtUtils) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    Cookie cookie = new Cookie("auth", jwt);
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(Integer.MAX_VALUE);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/signup")
  public UserResponse createUser(@RequestBody User user) {
    return userService.createUser(user);
  }
}
