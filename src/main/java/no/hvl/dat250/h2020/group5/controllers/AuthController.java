package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final CreateCookie createCookie;

  @Value("${poll.app.test.environment}")
  private Boolean isTest;

  public AuthController(UserService userService, CreateCookie createCookie) {
    this.userService = userService;
    this.createCookie = createCookie;
  }

  @PostMapping("/signin")
  public UserResponse authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    UserResponse userResponse = userService.getUserAccountByEmail(loginRequest.getEmail());
    userResponse.setRoles(
        createCookie.signIn(loginRequest.getEmail(), loginRequest.getPassword(), response));
    return userResponse;
  }

  @PostMapping("/signup")
  public UserResponse createUser(@RequestBody User user, HttpServletResponse response) {
    String rawPassword = user.getPassword();
    UserResponse savedUser = userService.createAccount(user);
    savedUser.setRoles(createCookie.signIn(user.getEmail(), rawPassword, response));
    return savedUser;
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
            + Integer.MIN_VALUE);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
