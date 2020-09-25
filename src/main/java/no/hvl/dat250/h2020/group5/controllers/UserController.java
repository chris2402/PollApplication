package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.service.PollService;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final PollService pollService;

    public UserController(UserService userService, PollService pollService) {
        this.userService = userService;
        this.pollService = pollService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id){
        return userService.getUser(id).orElse(null);
    }

    @PatchMapping("/{id}")
    public Boolean updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest){
        return userService.updateUser(id, updateUserRequest);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @RequestMapping(path = "/{id}/polls")
    public List<Poll> getUserPolls(@PathVariable Long id){
        return pollService.getUserPolls(id);
    }

}
