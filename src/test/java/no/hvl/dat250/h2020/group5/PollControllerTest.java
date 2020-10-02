package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.controllers.PollController;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PollController.class)
public class PollControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PollService pollService;

  private PollResponse pollResponse;
  private Poll poll1;
  private Poll poll2;

  @BeforeEach
  public void setUp() {
    poll1 = new Poll();
    poll1.setId(1L);
    poll1.setQuestion("my_awesome_name");
    poll1.setVisibilityType(PollVisibilityType.PRIVATE);

    poll2 = new Poll();
    poll2.setId(2L);
    poll2.setQuestion("my_awesome_name");
    poll2.setVisibilityType(PollVisibilityType.PUBLIC);
  }

  @Test
  public void shouldReturnAllPublicPollsTest() throws Exception {
    when(pollService.getAllPublicPolls())
        .thenReturn(Collections.singletonList(new PollResponse(poll2)));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/polls").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].visibilityType", is("PUBLIC")));
  }
}
