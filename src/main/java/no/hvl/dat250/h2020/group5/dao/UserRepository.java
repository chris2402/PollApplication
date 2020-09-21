package no.hvl.dat250.h2020.group5.dao;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {
    User createUser(String name, String password);
    boolean deleteUser(String userId);

    List<User> getAllUsers();
    User getUser(String userId);

    boolean updateUsername(String userId, String newName);
    boolean updatePassword(String userId, String oldPassword, String newPassword);
}
