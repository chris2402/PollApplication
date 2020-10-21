package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('USER')")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final ExtractIdFromAuth extractIdFromAuth;

  public UserController(UserService userService, ExtractIdFromAuth extractIdFromAuth) {
    this.userService = userService;
    this.extractIdFromAuth = extractIdFromAuth;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<UserResponse> getUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/me")
  public UserResponse getMe(Authentication authentication) {
    UserResponse user = userService.getUser(extractIdFromAuth.getIdFromAuth(authentication));
    user.setRoles(
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    return user;
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @PatchMapping("/{id}")
  public Boolean updateUser(
      @PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
    return userService.updateUser(id, updateUserRequest);
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public Boolean deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
  }
}
