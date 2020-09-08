package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.doa.UserDOA;
import no.hvl.dat250.h2020.group5.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

public class UserService implements UserDOA {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User createUser(String name, String password) {
        return null;
    }

    @Override
    public boolean deleteUser(String id) {
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public boolean getUser(String id) {
        return false;
    }

    @Override
    public boolean updateUsername(String userId, String newName) {
        return false;
    }

    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        return false;
    }
}
