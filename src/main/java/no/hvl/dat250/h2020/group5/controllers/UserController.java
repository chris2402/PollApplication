package no.hvl.dat250.h2020.group5.controllers;


import no.hvl.dat250.h2020.group5.entities.User;
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


    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public Boolean updateUser(@PathVariable String id, String newPassword, String oldPassword, String username){
        if (!newPassword.isEmpty() || !oldPassword.isEmpty()){
            return userService.updatePassword(id, oldPassword, newPassword);
        } else if (!username.isEmpty()) {
            return userService.updateUsername(id, username);
        }

        return false;
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable String id){
        return userService.deleteUser(id);
    }

}
