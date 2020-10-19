package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.security.jwt.JwtUtils;
import no.hvl.dat250.h2020.group5.service.GuestService;
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
  private final GuestService guestService;

  public AuthController(
      UserService userService,
      AuthenticationManager authenticationManager,
      PasswordEncoder encoder,
      JwtUtils jwtUtils,
      GuestService guestService) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.guestService = guestService;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    signIn(loginRequest.getUsername(), loginRequest.getPassword(), response);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/signup")
  public UserResponse createUser(@RequestBody User user, HttpServletResponse response) {
    String rawPassword = user.getPassword();
    UserResponse savedUser = userService.createUser(user);
    signIn(user.getUsername(), rawPassword, response);
    return savedUser;
  }

  @PostMapping("/signup/guest")
  public GuestResponse createGuest(@RequestBody Guest guest, HttpServletResponse response) {
    GuestResponse savedGuest = guestService.createGuest(guest);
    signIn(guest.getUsername(), guest.getUsername(), response);
    return savedGuest;
  }

  private void signIn(String username, String password, HttpServletResponse response) {
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
  }
}
