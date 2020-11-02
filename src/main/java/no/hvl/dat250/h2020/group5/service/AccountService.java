package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.exceptions.UsernameAlreadyTakenException;
import no.hvl.dat250.h2020.group5.repositories.AccountRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.CreateUserRequest;
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
public class AccountService {

  final AccountRepository accountRepository;

  final VoteRepository voteRepository;

  final PasswordEncoder encoder;

  public AccountService(
      AccountRepository accountRepository, VoteRepository voteRepository, PasswordEncoder encoder) {
    this.accountRepository = accountRepository;
    this.voteRepository = voteRepository;
    this.encoder = encoder;
  }

  @Transactional
  public UserResponse createAccount(CreateUserRequest createUserRequest) {
    Optional<Account> existingAccount = accountRepository.findByEmail(createUserRequest.getEmail());
    if (existingAccount.isPresent()) {
      throw new UsernameAlreadyTakenException("A user with that email already exists");
    }

    User user = new User();
    if (createUserRequest.getDisplayName() != null
        && !createUserRequest.getDisplayName().isEmpty()) {
      user.setDisplayName(createUserRequest.getDisplayName());
    }

    Account account = new Account();
    account.setPassword(encoder.encode(createUserRequest.getPassword()));
    account.setEmail(createUserRequest.getEmail());
    account.setUserAndAddThisToUser(user);

    Account savedAccount = accountRepository.save(account);

    return new UserResponse(savedAccount);
  }

  @Transactional
  public boolean deleteAccount(Long userId) {
    Optional<Account> account = accountRepository.findById(userId);

    if (account.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    List<Vote> votes = new ArrayList<>(account.get().getUser().getVotes());
    for (Vote vote : votes) {
      vote.setVoterAndAddThisVoteToVoter(null);
    }
    voteRepository.saveAll(votes);
    accountRepository.delete(account.get());
    return true;
  }

  public List<UserResponse> getAllAccounts() {
    return accountRepository.findAll().stream().map(UserResponse::new).collect(Collectors.toList());
  }

  public UserResponse getAccountByEmail(String email) {
    Optional<Account> account = accountRepository.findByEmail(email);
    if (account.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return new UserResponse(account.get());
  }

  public UserResponse getAccount(Long userId) {
    Optional<Account> account = accountRepository.findById(userId);
    if (account.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return new UserResponse(account.get());
  }

  public boolean updateAccount(Long userId, UpdateUserRequest updateUserRequest, Long authId) {
    Optional<Account> account = accountRepository.findById(userId);
    Optional<Account> authUser = accountRepository.findById(authId);
    boolean changesMade = false;

    if (account.isEmpty()) {
      return false;
    }

    if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
      account.get().setEmail(updateUserRequest.getEmail());
      changesMade = true;
    }

    if (updateUserRequest.getOldPassword() != null
        && updateUserRequest.getNewPassword() != null
        && encoder.matches(updateUserRequest.getOldPassword(), account.get().getPassword())) {
      account.get().setPassword(encoder.encode(updateUserRequest.getNewPassword()));
      changesMade = true;
    }

    if (updateUserRequest.getIsAdmin() != null
        && authUser.isPresent()
        && authUser.get().getIsAdmin()) {
      account.get().setIsAdmin(updateUserRequest.getIsAdmin());
      changesMade = true;
    }

    if (changesMade) {
      accountRepository.save(account.get());
    }

    return changesMade;
  }
}
