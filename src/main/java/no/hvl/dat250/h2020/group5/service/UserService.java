package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

  final UserRepository userRepository;

  final VoteRepository voteRepository;

  final PasswordEncoder encoder;

  public UserService(
      UserRepository userRepository,
      VoteRepository voteRepository,
      PollRepository pollRepository,
      PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.encoder = encoder;
  }

  public UserResponse createUser(User user) {
    user.setPassword(encoder.encode(user.getPassword()));
    return new UserResponse(userRepository.save(user));
  }

  @Transactional
  public boolean deleteUser(Long userId) {
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
      return false;
    }

    List<Vote> votes = new ArrayList<>(user.get().getVotes());
    for (Vote vote : votes) {
      vote.setVoterAndAddThisVoteToVoter(null);
    }
    voteRepository.saveAll(votes);
    userRepository.delete(user.get());
    return true;
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
  }

  public UserResponse getUserByUsername(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isEmpty()) {
      return null;
    }
    return new UserResponse(user.get());
  }

  public UserResponse getUser(Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
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
        && encoder.matches(updateUserRequest.getOldPassword(), user.get().getPassword())) {
      user.get().setPassword(encoder.encode(updateUserRequest.getNewPassword()));
      changesMade = true;
    }

    if (changesMade) {
      userRepository.save(user.get());
    }

    return changesMade;
  }
}
