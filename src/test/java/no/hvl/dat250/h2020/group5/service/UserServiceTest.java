package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
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

  private User user1;
  private User user2;
  private Vote vote;

  @BeforeEach
  public void setUp() {
    user1 = new User().password("password");
    user1.setId(1L);
    user2 = new User();
    user2.setId(2L);

    vote = new Vote();
    vote.setVoterAndAddThisVoteToVoter(user1);

    when(userRepository.save(any(User.class))).thenReturn(new User());
    when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
    when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
    when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

    when(passwordEncoder.encode(anyString())).thenReturn("HashedString");
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    when(voteRepository.findByVoter(user1)).thenReturn(Collections.singletonList(vote));
    when(voteRepository.save(any(Vote.class))).thenReturn(vote);
  }

  @Test
  public void shouldCreateUserTest() {
    Assertions.assertNotNull(userService.createUser(user1));
    Assertions.assertEquals(UserResponse.class, userService.createUser(new User()).getClass());
  }

  @Test
  public void shouldDeleteUserAndUserVotesTest() {
    Assertions.assertNotNull(vote.getVoter());
    Assertions.assertTrue(userService.deleteUser(vote.getVoter().getId()));
    Assertions.assertNull(vote.getVoter());
    verify(voteRepository, times(1)).saveAll(anyIterable());
    verify(userRepository, times(1)).delete(user1);
  }

  @Test
  public void shouldGiveAllUsersTest() {
    Assertions.assertEquals(2, userService.getAllUsers().size());
  }

  @Test
  public void shouldFindUserByUserId() {
    Assertions.assertEquals(user1.getId(), userService.getUser(user1.getId()).getId());
  }

  @Test
  public void shouldUpdateUserNameWithValidUpdateUserRequestTest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setUsername("New username");

    Assertions.assertTrue(userService.updateUser(user1.getId(), updateUserRequest, anyLong()));
    Assertions.assertEquals(updateUserRequest.getUsername(), user1.getUsername());
    verify(userRepository, times(1)).save(user1);
  }

  @Test
  public void shouldUpdatePasswordWithValidUpdateUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertTrue(userService.updateUser(user1.getId(), updateUserRequest, anyLong()));
    Assertions.assertEquals("HashedString", user1.getPassword());
    verify(userRepository, times(1)).save(user1);
  }

  @Test
  public void shouldNotUpdateUserWithInvalidUserRequest() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();

    Assertions.assertFalse(userService.updateUser(user1.getId(), updateUserRequest, anyLong()));
    verify(userRepository, times(0)).save(user1);
  }

  @Test
  public void shouldNotUpdateUserWhenUserDoesNotExists() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setNewPassword("New password");
    updateUserRequest.setOldPassword("password");

    Assertions.assertFalse(userService.updateUser(3L, updateUserRequest, anyLong()));
    verify(userRepository, times(0)).save(user1);
  }

  @Test
  public void shouldNotDeleteVotesWhenUserIsDeletedTest() {
    List<Vote> userVotes = new ArrayList<>(user1.getVotes());
    userService.deleteUser(user1.getId());
    verify(voteRepository, times(1)).saveAll(userVotes);
  }
}
