package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.requests.CreateUserRequest;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AccountService accountService;
  private final CreateCookie createCookie;

  @Value("${poll.app.test.environment}")
  private Boolean isTest;

  public AuthController(AccountService accountService, CreateCookie createCookie) {
    this.accountService = accountService;
    this.createCookie = createCookie;
  }

  @PostMapping("/signin")
  public UserResponse authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    UserResponse userResponse = accountService.getAccountByEmail(loginRequest.getEmail());
    userResponse.setRoles(
        createCookie.signIn(loginRequest.getEmail(), loginRequest.getPassword(), response));
    return userResponse;
  }

  @PostMapping("/signup")
  public UserResponse createUser(
      @RequestBody CreateUserRequest createUserRequest, HttpServletResponse response) {
    UserResponse savedUser = accountService.createAccount(createUserRequest);
    savedUser.setRoles(
        createCookie.signIn(
            createUserRequest.getEmail(), createUserRequest.getPassword(), response));
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
            + Integer.MAX_VALUE);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
