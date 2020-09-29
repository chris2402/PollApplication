package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PollServiceTest {

    @InjectMocks PollService pollService;

    @Mock PollRepository pollRepository;

    @Mock UserRepository userRepository;

    private User user;
    private Poll poll;

    @BeforeEach
    public void setUp() {
        this.user = new User();
        this.poll = new Poll();
    }

    @Test
    public void shouldCreateANewPollTest() {
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        pollService.createPoll(poll, user.getId());
        verify(pollRepository, times(1)).save(poll);
    }

    @Test
    public void shouldNotCreateANewPollIfUserIsNotPresentTest() {
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.empty());
        pollService.createPoll(poll, user.getId());
        verify(pollRepository, times(0)).save(poll);
    }

    @Test
    public void shouldDeleteAPollTest() {
        Poll poll = new Poll();
    }
}
