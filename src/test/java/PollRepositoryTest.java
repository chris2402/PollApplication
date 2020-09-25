import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = Main.class)
@DataJpaTest
public class PollRepositoryTest {

    @Autowired PollRepository pollRepository;
    private Poll poll;
    private List<Vote> votes;

    @BeforeEach
    public void setUp() {
        this.poll = new Poll();
        this.votes = Arrays.asList(new Vote(), new Vote(), new Vote());

        poll.setVotes(votes);
        pollRepository.save(poll);
    }

    @Test
    public void shouldPersistOnePollTest() {
        pollRepository.save(poll);
        Assertions.assertEquals(1, pollRepository.count());
    }

    @Test
    public void shouldHaveThreeVotesWhenThreeVotesAreAddedTest() {
        Optional<Poll> pollWithVotes = pollRepository.findById(poll.getId());
        Assertions.assertEquals(3, pollWithVotes.get().getVotes().size());
    }

    @Test
    public void shouldDeletePollTest() {
        pollRepository.deleteById(poll.getId());
        Assertions.assertEquals(0, pollRepository.count());
    }
}
