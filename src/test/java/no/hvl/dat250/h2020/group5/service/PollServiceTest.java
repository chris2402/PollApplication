package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

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

    @Test
    public void shouldReturnAllPublicPollsTest() {
        when(pollRepository.findAllByVisibilityType(PollVisibilityType.PUBLIC))
                .thenReturn(
                        Arrays.asList(
                                new Poll().visibilityType(PollVisibilityType.PUBLIC),
                                new Poll().visibilityType(PollVisibilityType.PUBLIC),
                                new Poll().visibilityType(PollVisibilityType.PUBLIC)));
        Assertions.assertEquals(3, pollService.getAllPublicPolls().size());
        Assertions.assertEquals(
                PollVisibilityType.PUBLIC,
                pollService.getAllPublicPolls().get(0).getVisibilityType());
    }

    @Test
    public void shouldGetPollResultTest() {
        poll.setVotes(
                Arrays.asList(
                        new Vote().answer(AnswerType.YES),
                        new Vote().answer(AnswerType.YES),
                        new Vote().answer(AnswerType.NO)));
        when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
        VotesResponse votes = pollService.getNumberOfVotes(poll.getId());
        Assertions.assertEquals(1, votes.getNo());
        Assertions.assertEquals(2, votes.getYes());
    }

    @Test
    public void shouldActivateAPollTest() {
        poll.setPollDuration(10000000);
        when(pollRepository.findById(poll.getId())).thenReturn(java.util.Optional.ofNullable(poll));
        pollService.activatePoll(poll.getId());
        Assertions.assertNotNull(poll.getStartTime());
        Assertions.assertTrue(pollService.isActivated(poll.getId()));
    }

    @Test
    public void shouldGetAllPollsByOwnerTest() {
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.ofNullable(user));
        List<Poll> polls =
                Arrays.asList(
                        poll.pollOwner(user),
                        new Poll().pollOwner(user),
                        new Poll().pollOwner(user));
        when(pollRepository.findAllByPollOwner(user)).thenReturn(polls);

        List<PollResponse> pollsFromService = pollService.getUserPolls(user.getId());

        Assertions.assertEquals(3, pollsFromService.size());
    }
}
