import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.dao.PollRepository;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.dao.VoterRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes=Main.class)
@DataJpaTest
public class VoteRepositoryTest {

    @Autowired
    VoteRepository repository;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoterRepository voterRepository;

    @Test
    public void shouldPersistAVoteTest() {
        Vote vote = new Vote();
        repository.save(vote);

        Assertions.assertEquals(1, repository.count());
    }

    @Test
    public void shouldAddVoteToPollTest() {
        Vote vote = new Vote();

        Poll poll = new Poll();
        Voter voter = new Guest();

        vote.setPoll(poll);
        vote.setVoter(voter);
        vote.setAnswer(AnswerType.YES);

        pollRepository.save(poll);
        voterRepository.save(voter);
        repository.save(vote);

        Assertions.assertEquals(vote, repository.findByPollAndAnswer(poll, AnswerType.YES).get(0));
    }

}