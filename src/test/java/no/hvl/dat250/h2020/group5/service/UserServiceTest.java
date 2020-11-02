package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.repositories.VoteRepository;
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
public class UserServiceTest {

  @InjectMocks UserService userService;

  @Mock UserRepository userRepository;

  @Mock VoteRepository voteRepository;

  @Mock PasswordEncoder passwordEncoder;

  private User user;
  private User user2;

  private Vote vote;
  private User createUser;

  @BeforeEach
  public void setUp() {
    user = new User().password("password");
    user.setId(UUID.randomUUID());

    user2 = new User().password("password");
    user2.setId(UUID.randomUUID());

    createUser = new User().displayName("test").password("password").email("test@test.com");

    vote = new Vote();
    vote.setVoterAndAddThisVoteToVoter(user);

    when(userRepository.save(any(User.class))).thenReturn(new User());
    when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
    when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
    when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

    when(passwordEncoder.encode(anyString())).thenReturn("HashedString");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    when(voteRepository.findByVoter(user)).thenReturn(Collections.singletonList(vote));
    when(voteRepository.save(any(Vote.class))).thenReturn(vote);
  }

  @Test
  public void shouldCreateUserTest() {
    Assertions.assertNotNull(userService.createAccount(createUser));
    Assertions.assertEquals(UserResponse.class, userService.createAccount(createUser).getClass());
  }

  @Test
  public void shouldDeleteUserAndUserVotesTest() {
    Assertions.assertNotNull(vote.getVoter());
    Assertions.assertTrue(userService.deleteUser(user.getId()));
    Assertions.assertNull(vote.getVoter());
    verify(voteRepository, times(1)).saveAll(anyIterable());
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  public void shouldGiveAllUsersTest() {
    Assertions.assertEquals(2, userService.getAllUserAccounts().size());
  }

  @Test
  public void shouldFindUserByUserId() {
    Assertions.assertEquals(user.getId(), userService.getUser(user.getId()).getId());
  }

  @Test
  public void shouldUpdateUserNameWithValidUpdateUserRequestTest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setEmail("New email");

    Assertions.assertTrue(
        userService.updateAccount(user.getId(), updateUserRequest, any(UUID.class)));
    Assertions.assertEquals(updateUserRequest.getEmail(), user.getEmail());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  public void shouldUpdatePasswordWithValidUpdateUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertTrue(
        userService.updateAccount(user.getId(), updateUserRequest, any(UUID.class)));
    Assertions.assertEquals("HashedString", user.getPassword());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  public void shouldNotUpdateUserWithInvalidUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();

    Assertions.assertFalse(
        userService.updateAccount(user.getId(), updateUserRequest, any(UUID.class)));
    verify(userRepository, times(0)).save(user);
  }

  @Test
  public void shouldNotUpdateUserWhenUserDoesNotExists() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertThrows(
        NotFoundException.class,
        () -> userService.updateAccount(UUID.randomUUID(), updateUserRequest, any(UUID.class)));
    verify(userRepository, times(0)).save(user);
  }

  @Test
  public void shouldNotDeleteVotesWhenUserIsDeletedTest() {
    List<Vote> userVotes = new ArrayList<>(user.getVotes());
    userService.deleteUser(user.getId());
    verify(voteRepository, times(1)).saveAll(userVotes);
  }
}
