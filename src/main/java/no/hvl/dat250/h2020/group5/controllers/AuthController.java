package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final VotingDeviceService votingDeviceService;
  private final UserService userService;
  private final GuestService guestService;
  private final CreateCookie createCookie;

  public AuthController(
      VotingDeviceService votingDeviceService,
      UserService userService,
      GuestService guestService,
      CreateCookie createCookie) {
    this.votingDeviceService = votingDeviceService;
    this.userService = userService;
    this.guestService = guestService;
    this.createCookie = createCookie;
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    createCookie.signIn(loginRequest.getUsername(), loginRequest.getPassword(), response);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/signup")
  public UserResponse createUser(@RequestBody User user, HttpServletResponse response) {
    String rawPassword = user.getPassword();
    UserResponse savedUser = userService.createUser(user);
    createCookie.signIn(user.getUsername(), rawPassword, response);
    return savedUser;
  }

  @PostMapping("/signup/guest")
  public GuestResponse createGuest(@RequestBody Guest guest, HttpServletResponse response) {
    GuestResponse savedGuest = guestService.createGuest(guest);
    createCookie.signIn(guest.getUsername(), guest.getUsername(), response);
    return savedGuest;
  }

  @PostMapping("/signin/guest")
  public ResponseEntity<?> signGuest(@RequestBody Guest guest, HttpServletResponse response) {
    createCookie.signIn(guest.getUsername(), guest.getUsername(), response);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/signup/device")
  public VotingDeviceResponse createDevice(HttpServletResponse response) {
    VotingDeviceResponse savedDevice = votingDeviceService.createDevice();
    savedDevice.setJwt(
        createCookie.signIn(savedDevice.getUsername(), savedDevice.getUsername(), response));
    return savedDevice;
  }

  @PostMapping("/signin/device")
  public VotingDeviceResponse loginDevice(
      @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    VotingDeviceResponse votingDevice = votingDeviceService.findDevice(loginRequest.getUsername());
    votingDevice.setJwt(
        createCookie.signIn(votingDevice.getUsername(), votingDevice.getUsername(), response));
    return votingDevice;
  }
}
