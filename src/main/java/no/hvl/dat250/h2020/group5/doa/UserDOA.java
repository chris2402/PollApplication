package no.hvl.dat250.h2020.group5.doa;

import no.hvl.dat250.h2020.group5.entities.User;

import java.util.List;

public interface UserDOA {
    User createUser(String name, String password);
    boolean deleteUser(String id);

    List<User> getAllUsers();
    boolean getUser(String id);

    boolean updateUsername(String userId, String newName);
    boolean updatePassword(String userId, String oldPassword, String newPassword);
}
