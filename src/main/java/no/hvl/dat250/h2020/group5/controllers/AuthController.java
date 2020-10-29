package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.exceptions.UsernameAlreadyTakenException;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final VotingDeviceService votingDeviceService;
  private final UserService userService;
  private final GuestService guestService;
  private final CreateCookie createCookie;
  private final ExtractIdFromAuth extractIdFromAuth;

  @Value("${poll.app.test.environment}")
  private Boolean isTest;

  public AuthController(
      VotingDeviceService votingDeviceService,
      UserService userService,
      GuestService guestService,
      CreateCookie createCookie,
      ExtractIdFromAuth extractIdFromAuth) {
    this.votingDeviceService = votingDeviceService;
    this.userService = userService;
    this.guestService = guestService;
    this.createCookie = createCookie;
    this.extractIdFromAuth = extractIdFromAuth;
  }

  @PostMapping("/signin")
  public UserResponse authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    UserResponse userResponse = userService.getUserByUsername(loginRequest.getUsername());
    userResponse.setRoles(
        createCookie.signIn(loginRequest.getUsername(), loginRequest.getPassword(), response));
    return userResponse;
  }

  @PostMapping("/signup")
  public UserResponse createUser(@RequestBody User user, HttpServletResponse response) {
    String rawPassword = user.getPassword();
    UserResponse savedUser = userService.createUser(user);
    savedUser.setRoles(createCookie.signIn(user.getUsername(), rawPassword, response));
    return savedUser;
  }

  @PostMapping("/signup/guest")
  public GuestResponse createGuest(@RequestBody Guest guest, HttpServletResponse response) {
    GuestResponse savedGuest = guestService.createGuest(guest);
    savedGuest.setRoles(createCookie.signIn(guest.getUsername(), guest.getUsername(), response));
    return savedGuest;
  }

  @PostMapping("/signin/guest")
  public GuestResponse signGuest(@RequestBody Guest guest, HttpServletResponse response) {
    GuestResponse guestResponse = new GuestResponse(guest);
    guestResponse.setRoles(createCookie.signIn(guest.getUsername(), guest.getUsername(), response));
    return guestResponse;
  }

  @PostMapping("/logout")
  public ResponseEntity<?> loginDevice(HttpServletResponse response) {
    response.setHeader(
        "Set-Cookie",
        "auth=;"
            + "path=/;"
            + (isTest ? "" : "SameSite=None;")
            + (isTest ? "" : "Secure;")
            + "HttpOnly;"
            + "Max-Age="
            + Integer.MAX_VALUE);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler({UsernameAlreadyTakenException.class})
  public ResponseEntity<Object> handleUserNotFoundException(
      UsernameAlreadyTakenException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
