package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.exceptions.UsernameAlreadyTakenException;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

  final UserRepository userRepository;
  final VoteRepository voteRepository;

  final PasswordEncoder encoder;

  public UserService(
      UserRepository userRepository, VoteRepository voteRepository, PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.encoder = encoder;
  }

  @Transactional
  public UserResponse createAccount(User user) {
    Optional<User> existingAccount = userRepository.findByEmail(user.getEmail());
    if (existingAccount.isPresent()) {
      throw new UsernameAlreadyTakenException("A user with that email already exists");
    }

    user.setPassword(encoder.encode(user.getPassword()));

    return new UserResponse(userRepository.save(user));
  }

  @Transactional
  public boolean deleteUser(UUID userId) {
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    List<Vote> votes = new ArrayList<>(user.get().getVotes());
    for (Vote vote : votes) {
      vote.setVoterAndAddThisVoteToVoter(null);
    }
    voteRepository.saveAll(votes);
    userRepository.delete(user.get());
    return true;
  }

  public List<UserResponse> getAllUserAccounts() {
    return userRepository.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
  }

  public UserResponse getUserAccountByEmail(String email) {
    Optional<User> account = userRepository.findByEmail(email);
    if (account.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return new UserResponse(account.get());
  }

  public UserResponse getUser(UUID userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return new UserResponse(user.get());
  }

  public boolean updateAccount(UUID userId, UpdateUserRequest updateUserRequest, Boolean isAdmin) {
    Optional<User> user = userRepository.findById(userId);
    boolean changesMade = false;

    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
      user.get().setEmail(updateUserRequest.getEmail());
      changesMade = true;
    }

    if (updateUserRequest.getOldPassword() != null
        && updateUserRequest.getNewPassword() != null
        && encoder.matches(updateUserRequest.getOldPassword(), user.get().getPassword())) {
      user.get().setPassword(encoder.encode(updateUserRequest.getNewPassword()));
      changesMade = true;
    }

    if (isAdmin) {
      user.get().setIsAdmin(updateUserRequest.getIsAdmin());
      changesMade = true;
    }

    if (changesMade) {
      userRepository.save(user.get());
    }

    return changesMade;
  }
}
