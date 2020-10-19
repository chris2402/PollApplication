package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class})
@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  private UserResponse userResponse;

  @BeforeEach
  public void setUp() {
    User user = new User();
    user.setId(1L);
    user.setUsername("my_awesome_name");
    this.userResponse = new UserResponse(user);
    when(userService.getUser(1L)).thenReturn(userResponse);
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
    when(userService.updateUser(any(), any())).thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/users/1")
                .content(
                    "{\"username\":\"my_awesome_username\", \"oldPassword\":\"my_password\"}, \"newPassword\":\"my_new_awesome_password\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  public void shouldGiveAllUsersTest() throws Exception {
    when(userService.getAllUsers())
        .thenReturn(
            Arrays.asList(
                new UserResponse(new User().userName("user1").admin(true).password("abcde")),
                new UserResponse(new User().userName("user2").admin(false).password("1234")),
                new UserResponse(
                    new User().userName("user3").admin(false).password("my_cool_password"))));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    "[{\"id\":null,\"username\":\"user1\",\"isAdmin\":true},{\"id\":null,\"username\":\"user2\",\"isAdmin\":false},{\"id\":null,\"username\":\"user3\",\"isAdmin\":false}]"));
  }
}
