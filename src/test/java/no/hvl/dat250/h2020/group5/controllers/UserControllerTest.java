package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.responses.UserResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import no.hvl.dat250.h2020.group5.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;

    @MockBean private PollService pollService;

    private UserResponse userResponse;
    private User user;

    @BeforeEach
    public void setUp() {
        this.user = new User();
        user.setId(1L);
        user.setUsername("my_awesome_name");
        this.userResponse = new UserResponse(user);
        when(userService.getUser(1L)).thenReturn(userResponse);
    }

    @Test
    public void shouldReturnOneUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(
                                        "{\"id\":1, \"username\":my_awesome_name, \"isAdmin\":false}"));
    }

    @Test
    public void shouldCreateUserTest() throws Exception {
        when(userService.createUser(any())).thenReturn(userResponse);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(
                                        "{\"username\":\"my_awesome_name\", \"password\":\"my_password\", \"isAdmin\":\"false\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        content()
                                .json(
                                        "{\"id\":1, \"username\":my_awesome_name, \"isAdmin\":false}"));
    }

    @Test
    public void shouldDeleteUserTest() throws Exception {
        when(userService.deleteUser(any())).thenReturn(true);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void shouldUpdateUserTest() throws Exception {
        when(userService.updateUser(any(), any())).thenReturn(true);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(
                                        "{\"username\":\"my_awesome_username\", \"oldPassword\":\"my_password\"}, \"newPassword\":\"my_new_awesome_password\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
