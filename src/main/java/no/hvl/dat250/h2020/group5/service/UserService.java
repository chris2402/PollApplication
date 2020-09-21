package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;


    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(String userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()){
            return false;
        }

        List<Vote> votes = voteRepository.findByUserId(userId);
        for (Vote vote : votes) {
            vote.setVoter(null);
        }
        voteRepository.saveAll(votes);
        userRepository.delete(user.get());
        return true;

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUsername(String userId, String newName) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty() || newName.isEmpty()){
            return false;
        }

        user.get().setUserName(newName);
        userRepository.save(user.get());
        return true;
    }

    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty() || !user.get().getPassword().equals(oldPassword) || newPassword.length() < 6) {
            return false;
        }
        user.get().setPassword(newPassword);
        userRepository.save(user.get());
        return true;

    }

}
