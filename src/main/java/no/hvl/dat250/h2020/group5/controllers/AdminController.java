package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
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
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id).orElse(null);
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
    public List<Poll> getAllUserPolls(@PathVariable Long id) {
        return pollService.getUserPolls(id);
    }

    @GetMapping("/polls")
    public List<Poll> getAllPolls() {
        return pollService.getAllPolls();
    }

    @GetMapping("/polls/{id}")
    public Poll getPoll(@PathVariable Long id) {
        return pollService.getPoll(id);
    }

    @DeleteMapping(path = "/polls/{id}/{userId}")
    public boolean deletePoll(@PathVariable Long id, @PathVariable Long userId) {
        return pollService.deletePoll(id, userId);
    }
}
