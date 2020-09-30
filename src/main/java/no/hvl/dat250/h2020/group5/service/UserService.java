package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final UserRepository userRepository;

    final VoteRepository voteRepository;

    public UserService(
            UserRepository userRepository,
            VoteRepository voteRepository,
            PollRepository pollRepository) {
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    public UserResponse createUser(User user) {
        return  new UserResponse(userRepository.save(user));
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
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

    public List<UserResponse> getAllUsers() {
        List<UserResponse> userResponseList = new ArrayList<>();
        userRepository.findAll().forEach(user -> userResponseList.add(createUser(user)));
        return userResponseList;
    }

    public UserResponse getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            return null;
        }
        return new UserResponse(user.get());
    }

    public boolean updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        Optional<User> user = userRepository.findById(userId);
        boolean changesMade = false;

        if (user.isEmpty()) {
            return false;
        }

        if (updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().isEmpty()) {
            user.get().setUsername(updateUserRequest.getUsername());
            changesMade = true;
        }

        if (updateUserRequest.getOldPassword() != null
                && updateUserRequest.getNewPassword() != null
                && updateUserRequest.getOldPassword().equals(user.get().getPassword())) {
            user.get().setPassword(updateUserRequest.getNewPassword());
            changesMade = true;
        }

        if (changesMade) {
            userRepository.save(user.get());
        }

        return changesMade;
    }
}
