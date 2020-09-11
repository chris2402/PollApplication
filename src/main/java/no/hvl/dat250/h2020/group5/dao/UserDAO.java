package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.User;

import java.util.List;

public interface UserDAO {
    User createUser(String name, String password);
    boolean deleteUser(String userId);

    List<User> getAllUsers();
    User getUser(String userId);

    boolean updateUsername(String userId, String newName);
    boolean updatePassword(String userId, String oldPassword, String newPassword);
}
