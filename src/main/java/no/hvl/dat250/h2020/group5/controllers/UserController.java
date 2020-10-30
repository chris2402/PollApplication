package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAuthority('USER')")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<UserResponse> getUsers() {
    return userService.getAllUsers();
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @PatchMapping("/{id}")
  public Boolean updateUser(
      @PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest, Authentication authentication) {
    return userService.updateUser(id, updateUserRequest, extractIdFromAuth.getIdFromAuth(authentication));
  }

  @PreAuthorize("authentication.principal.id == #id or hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public Boolean deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
  }
}
