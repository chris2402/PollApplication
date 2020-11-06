package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserController.class})
@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserService userService;
  @MockBean private ExtractFromAuth extractFromAuth;

  private UserResponse userResponse;
  private User user;

  @BeforeEach
  public void setUp() {
    user = new User().email("my_awesome_name").displayName("My displayName");
    user.setId(UUID.randomUUID());

    this.userResponse = new UserResponse(user);
    when(userService.getUser(eq(user.getId()))).thenReturn(userResponse);
  }

  @Test
  public void shouldReturnCurrentlyLoggedInUserTest() throws Exception {
    when(extractFromAuth.userId(any(Authentication.class))).thenReturn(user.getId());
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/me").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));
  }

  @Test
  public void shouldReturnOneAccountTest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/users/" + user.getId()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));
  }

  @Test
  public void shouldDeleteAccountTest() throws Exception {
    UUID id = UUID.randomUUID();
    when(userService.deleteUser(eq(id))).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/users/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldUpdateAccountTest() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest().email("new_name").oldPassword("old").newPassword("new");
    when(userService.updateAccount(any(UUID.class), any(UpdateUserRequest.class), eq(true)))
        .thenReturn(true);
    when(extractFromAuth.isAdmin(any(Authentication.class))).thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/users/" + user.getId())
                .content(objectMapper.writeValueAsString(updateUserRequest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldGiveAllUsersTest() throws Exception {
    List<UserResponse> response =
        Arrays.asList(
            new UserResponse(new User().email("user1").admin(true).password("abcde")),
            new UserResponse(new User().email("user2").admin(false).password("1234")),
            new UserResponse(new User().email("user3").admin(false).password("my_cool_password")));
    when(userService.getAllUserAccounts()).thenReturn(response);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(response));
  }
}
