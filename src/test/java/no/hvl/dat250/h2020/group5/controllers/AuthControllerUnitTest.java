package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.requests.CreateUserRequest;
import no.hvl.dat250.h2020.group5.requests.LoginRequest;
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

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {AuthController.class})
@WebMvcTest(AuthController.class)
@WithMockUser
public class AuthControllerUnitTest {

  @MockBean CreateCookie createCookie;
  @MockBean private ExtractFromAuth extractFromAuth;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserService userService;
  private UserResponse userResponse;

  @BeforeEach
  public void setUp() {
    User user = new User().displayName("hi").email("email").password("password");
    user.setId(UUID.randomUUID());
    this.userResponse = new UserResponse(user);
  }

  @Test
  public void shouldLoginUserAndAccountTest() throws Exception {
    LoginRequest loginRequest = new LoginRequest().email("email").password("password");
    when(userService.getUserAccountByEmail("email")).thenReturn(userResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/signin")
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));

    verify(createCookie, times(1)).signIn(anyString(), anyString(), any(HttpServletResponse.class));
  }

  @Test
  public void shouldCreateUserAndAccountTest() throws Exception {
    CreateUserRequest createUserRequest =
        new CreateUserRequest().email("email").displayName("hi").password("password");
    when(userService.createAccount(any(User.class))).thenReturn(userResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/signup")
                .content(objectMapper.writeValueAsString(createUserRequest))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(userResponse));

    verify(createCookie, times(1)).signIn(anyString(), anyString(), any(HttpServletResponse.class));
  }
}
