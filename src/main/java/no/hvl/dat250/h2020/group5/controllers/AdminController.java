package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    final UserService userService;

    final PollService pollService;

    public AdminController(UserService userService, PollService pollService) {
        this.userService = userService;
        this.pollService = pollService;
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PatchMapping("/users/{id}")
    public Boolean editUser(
            @PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(id, updateUserRequest);
    }

    @DeleteMapping("/users/{id}")
    public Boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/users/{id}/polls")
    public List<PollResponse> getAllUserPolls(@PathVariable Long id) {
        return pollService.getUserPolls(id);
    }

    @GetMapping("/polls")
    public List<PollResponse> getAllPolls() {
        return pollService.getAllPolls();
    }

    @GetMapping("/polls/{id}")
    public PollResponse getPoll(@PathVariable Long id) {
        return pollService.getPoll(id);
    }

    @DeleteMapping(path = "/polls/{id}/{userId}")
    public boolean deletePoll(@PathVariable Long id, @PathVariable Long userId) {
        return pollService.deletePoll(id, userId);
    }
}
