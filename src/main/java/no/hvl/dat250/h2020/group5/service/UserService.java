package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

  final UserRepository userRepository;

  final VoteRepository voteRepository;

  public UserService(
      UserRepository userRepository, VoteRepository voteRepository, PollRepository pollRepository) {
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
  }

  public UserResponse createUser(User user) {
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

  public List<UserResponse> getAllUsers(Long adminId) {
    Optional<User> maybeUser = userRepository.findById(adminId);
    if (maybeUser.isPresent() && maybeUser.get().getIsAdmin()) {
      return userRepository.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
    }
    return null;
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
