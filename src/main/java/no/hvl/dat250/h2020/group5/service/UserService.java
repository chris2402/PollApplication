package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.UserRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final
    UserRepository userRepository;

    final
    VoteRepository voteRepository;

    final
    PollRepository pollRepository;

    public UserService(UserRepository userRepository, VoteRepository voteRepository, PollRepository pollRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
    }


    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty()){
            return false;
        }

        List<Vote> votes = voteRepository.findByVoter(user.get());
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

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        Optional<User> user = userRepository.findById(userId);
        boolean changesMade = false;

        if(user.isEmpty()){
            return false;
        }

        if(updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().isEmpty()){
            user.get().setUsername(updateUserRequest.getUsername());
            changesMade = true;
        }

        if(updateUserRequest.getOldPassword() != null &&
           updateUserRequest.getNewPassword() != null &&
           updateUserRequest.getNewPassword().equals(user.get().getPassword())) {
            user.get().setPassword(updateUserRequest.getNewPassword());
            changesMade = true;
        }

        if (changesMade){
            userRepository.save(user.get());
        }

        return changesMade;
    }

    public List<Poll> getUserPolls(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> pollRepository.findAllByPollOwner(value)).orElse(null);
    }

}
