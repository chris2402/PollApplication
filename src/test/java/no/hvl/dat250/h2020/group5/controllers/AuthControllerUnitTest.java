package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
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

import static no.hvl.dat250.h2020.group5.controllers.ResponseBodyMatchers.responseBody;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {AuthController.class})
@WebMvcTest(AuthController.class)
@WithMockUser
public class AuthControllerUnitTest {

  @MockBean CreateCookie createCookie;
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private GuestService guestService;
  @MockBean private UserService userService;
  @MockBean private VotingDeviceService votingDeviceService;
  private UserResponse userResponse;
  private GuestResponse guestResponse1;

  @BeforeEach
  public void setUp() {
    Guest guest1 = new Guest().username("guest 123");
    guest1.setId(1L);
    guestResponse1 = new GuestResponse(guest1);

    User user = new User().userName("my_awesome_name");
    user.setId(1L);
    this.userResponse = new UserResponse(user);
  }

  @Test
  public void shouldCreateUserTest() throws Exception {
    when(userService.createUser(any(User.class))).thenReturn(userResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/signup")
                .content("{\"username\":\"my_awesome_name\", \"password\":\"my_password\"}")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"id\":1, \"username\":my_awesome_name, \"isAdmin\":false}"));

    verify(createCookie, times(1)).signIn(anyString(), anyString(), any(HttpServletResponse.class));
  }

  @Test
  public void shouldCreateGuestTest() throws Exception {
    when(guestService.createGuest(any(Guest.class))).thenReturn(guestResponse1);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/signup/guest")
                .with(csrf())
                .content(objectMapper.writeValueAsString(new Guest().username("guest")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(responseBody().containsObjectAsJson(guestResponse1));

    verify(createCookie, times(1)).signIn(anyString(), anyString(), any(HttpServletResponse.class));
  }
}
