package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class})
@WebMvcTest(UserController.class)
public class AuthControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  private UserResponse userResponse;

  @Test
  public void shouldCreateUserTest() throws Exception {
    when(userService.createUser(any())).thenReturn(userResponse);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(
                    "{\"username\":\"my_awesome_name\", \"password\":\"my_password\", \"isAdmin\":\"false\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"id\":1, \"username\":my_awesome_name, \"isAdmin\":false}"));
  }
}
