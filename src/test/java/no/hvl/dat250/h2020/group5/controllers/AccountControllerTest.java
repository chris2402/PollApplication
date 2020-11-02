package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.Account;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.UpdateUserRequest;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.AccountService;
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

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {AccountController.class})
@WebMvcTest(AccountController.class)
@WithMockUser
public class AccountControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AccountService accountService;
  @MockBean private ExtractFromAuth extractFromAuth;

  private UserResponse userResponse;

  @BeforeEach
  public void setUp() {
    User user = new User();
    user.setDisplayName("My displayName");
    Account account = new Account().email("my_awesome_name");
    account.setUserAndAddThisToUser(user);
    account.setId(1L);

    this.userResponse = new UserResponse(account);
    when(accountService.getAccount(eq(1L))).thenReturn(userResponse);
  }

  @Test
  public void shouldReturnCurrentlyLoggedInUserTest() throws Exception {
    when(extractFromAuth.accountId(any(Authentication.class))).thenReturn(1L);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/me").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));
  }

  @Test
  public void shouldReturnOneAccountTest() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));
  }

  @Test
  public void shouldDeleteAccountTest() throws Exception {
    when(accountService.deleteAccount(1L)).thenReturn(true);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(true));
  }

  @Test
  public void shouldUpdateAccountTest() throws Exception {
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest().email("new_name").oldPassword("old").newPassword("new");
    when(accountService.updateAccount(anyLong(), any(UpdateUserRequest.class), anyLong()))
        .thenReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/users/1")
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
            new UserResponse(new Account().email("user1").admin(true).password("abcde")),
            new UserResponse(new Account().email("user2").admin(false).password("1234")),
            new UserResponse(
                new Account().email("user3").admin(false).password("my_cool_password")));
    when(accountService.getAllAccounts()).thenReturn(response);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(response));
  }
}
