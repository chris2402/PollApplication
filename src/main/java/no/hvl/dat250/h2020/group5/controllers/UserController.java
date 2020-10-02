package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/admin/{adminId}")
  public List<UserResponse> getUsers(@PathVariable Long adminId) {
    return userService.getAllUsers(adminId);
  }

  @PostMapping
  public UserResponse createUser(@RequestBody User user) {
    return userService.createUser(user);
  }

  @GetMapping("/{id}")
  public UserResponse getUser(@PathVariable Long id) {
    return userService.getUser(id);
  }

  @PatchMapping("/{id}")
  public Boolean updateUser(
      @PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
    return userService.updateUser(id, updateUserRequest);
  }

  @DeleteMapping("/{id}")
  public Boolean deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
  }
}
