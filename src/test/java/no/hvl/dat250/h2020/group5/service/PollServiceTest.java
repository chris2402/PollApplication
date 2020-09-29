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
        user.setId(1L);
        poll.setId(2L);
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
    public void shouldDeleteAPollWhenUserIsOwnerTest() {
        poll.setPollOwner(user);
        when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        pollService.deletePoll(poll.getId(), user.getId());
        verify(pollRepository, times(1)).delete(poll);
    }

    @Test
    public void shouldDeleteAPollWhenUserIsAdminTest() {
        User pollOwner = new User();
        pollOwner.setId(3L);
        poll.setPollOwner(pollOwner);
        user.setIsAdmin(true);

        when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        pollService.deletePoll(poll.getId(), user.getId());

        verify(pollRepository, times(1)).delete(poll);
    }

    @Test
    public void shouldNotDeletePollWhenUserIsNotOwnerOrAdminTest() {
        User pollOwner = new User();
        pollOwner.setId(3L);
        poll.setPollOwner(pollOwner);

        when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        pollService.deletePoll(poll.getId(), user.getId());
        verify(pollRepository, times(0)).delete(poll);
    }
}
