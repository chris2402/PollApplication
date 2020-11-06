package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('USER')")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final ExtractFromAuth extractFromAuth;

  public UserController(UserService userService, ExtractFromAuth extractFromAuth) {
    this.userService = userService;
    this.extractFromAuth = extractFromAuth;
  }

  @GetMapping("/me")
  public UserResponse getMe(Authentication authentication) {
    UserResponse user = userService.getUser(extractFromAuth.userId(authentication));
    user.setRoles(
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    return user;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<UserResponse> getAccounts() {
    return userService.getAllUserAccounts();
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public UserResponse getUserAccount(@PathVariable UUID id) {
    return userService.getUser(id);
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @PatchMapping("/{id}")
  public Boolean updateUser(
      @PathVariable UUID id,
      @RequestBody UpdateUserRequest updateUserRequest,
      Authentication authentication) {
    return userService.updateAccount(
        id, updateUserRequest, extractFromAuth.isAdmin(authentication));
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public Boolean deleteUser(@PathVariable UUID id) {
    return userService.deleteUser(id);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
