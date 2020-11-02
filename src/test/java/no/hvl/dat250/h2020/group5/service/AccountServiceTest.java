package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.repositories.AccountRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
import no.hvl.dat250.h2020.group5.requests.CreateUserRequest;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {

  @InjectMocks AccountService accountService;

  @Mock AccountRepository accountRepository;

  @Mock VoteRepository voteRepository;

  @Mock PasswordEncoder passwordEncoder;

  private Account account1;
  private User user;
  private Account account2;
  private Vote vote;
  private CreateUserRequest createUserRequest;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    account1 = new Account().password("password");
    account1.setUser(user);
    account1.setId(1L);

    createUserRequest =
        new CreateUserRequest().displayName("test").password("password").email("test@test.com");

    account2 = new Account();
    account2.setId(2L);

    vote = new Vote();
    vote.setVoterAndAddThisVoteToVoter(account1.getUser());

    when(accountRepository.save(any(Account.class))).thenReturn(new Account());
    when(accountRepository.findById(account1.getId())).thenReturn(Optional.ofNullable(account1));
    when(accountRepository.findById(account2.getId())).thenReturn(Optional.ofNullable(account2));
    when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

    when(passwordEncoder.encode(anyString())).thenReturn("HashedString");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    when(voteRepository.findByVoter(account1.getUser()))
        .thenReturn(Collections.singletonList(vote));
    when(voteRepository.save(any(Vote.class))).thenReturn(vote);
  }

  @Test
  public void shouldCreateUserTest() {
    Assertions.assertNotNull(accountService.createAccount(createUserRequest));
    Assertions.assertEquals(
        UserResponse.class, accountService.createAccount(createUserRequest).getClass());
  }

  @Test
  public void shouldDeleteUserAndUserVotesTest() {
    Assertions.assertNotNull(vote.getVoter());
    Assertions.assertTrue(accountService.deleteAccount(account1.getId()));
    Assertions.assertNull(vote.getVoter());
    verify(voteRepository, times(1)).saveAll(anyIterable());
    verify(accountRepository, times(1)).delete(account1);
  }

  @Test
  public void shouldGiveAllUsersTest() {
    Assertions.assertEquals(2, accountService.getAllAccounts().size());
  }

  @Test
  public void shouldFindUserByUserId() {
    Assertions.assertEquals(account1.getId(), accountService.getAccount(account1.getId()).getId());
  }

  @Test
  public void shouldUpdateUserNameWithValidUpdateUserRequestTest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setEmail("New email");

    Assertions.assertTrue(
        accountService.updateAccount(account1.getId(), updateUserRequest, anyLong()));
    Assertions.assertEquals(updateUserRequest.getEmail(), account1.getEmail());
    verify(accountRepository, times(1)).save(account1);
  }

  @Test
  public void shouldUpdatePasswordWithValidUpdateUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertTrue(
        accountService.updateAccount(account1.getId(), updateUserRequest, anyLong()));
    Assertions.assertEquals("HashedString", account1.getPassword());
    verify(accountRepository, times(1)).save(account1);
  }

  @Test
  public void shouldNotUpdateUserWithInvalidUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();

    Assertions.assertFalse(
        accountService.updateAccount(account1.getId(), updateUserRequest, anyLong()));
    verify(accountRepository, times(0)).save(account1);
  }

  @Test
  public void shouldNotUpdateUserWhenUserDoesNotExists() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertFalse(accountService.updateAccount(3L, updateUserRequest, anyLong()));
    verify(accountRepository, times(0)).save(account1);
  }

  @Test
  public void shouldNotDeleteVotesWhenUserIsDeletedTest() {
    List<Vote> userVotes = new ArrayList<>(account1.getUser().getVotes());
    accountService.deleteAccount(account1.getId());
    verify(voteRepository, times(1)).saveAll(userVotes);
  }
}
