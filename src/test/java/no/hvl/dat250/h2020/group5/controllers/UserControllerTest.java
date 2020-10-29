package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.service.VoterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserController.class})
@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserService userService;
  @MockBean private ExtractIdFromAuth extractIdFromAuth;
  @MockBean private VoterService voterService;

  private UserResponse userResponse;

  @BeforeEach
  public void setUp() {
    User user = new User().userName("my_awesome_name");
    user.setId(1L);

    this.userResponse = new UserResponse(user);
    when(userService.getUser(eq(1L))).thenReturn(userResponse);
  }

  @Test
  public void shouldReturnOneUserTest() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"id\":1, \"username\":my_awesome_name, \"isAdmin\":false}"));
  }

  @Test
  public void shouldDeleteUserTest() throws Exception {
    when(userService.deleteUser(1L)).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void shouldUpdateUserTest() throws Exception {
    when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/users/1")
                .content(
                    "{\"username\":\"my_awesome_username\", \"oldPassword\":\"my_password\"}, \"newPassword\":\"my_new_awesome_password\"}")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void shouldGiveAllUsersTest() throws Exception {
    List<UserResponse> response =
        Arrays.asList(
            new UserResponse(new User().userName("user1").admin(true).password("abcde")),
            new UserResponse(new User().userName("user2").admin(false).password("1234")),
            new UserResponse(
                new User().userName("user3").admin(false).password("my_cool_password")));
    when(userService.getAllUsers()).thenReturn(response);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(response));
  }
}
