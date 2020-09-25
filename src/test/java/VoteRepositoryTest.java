import no.hvl.dat250.h2020.group5.Main;
import no.hvl.dat250.h2020.group5.dao.VoteRepository;
import no.hvl.dat250.h2020.group5.entities.Vote;
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

    @Test
    public void shouldPersistAVoteTest() {
        Vote vote = new Vote();
        repository.save(vote);

        Assertions.assertEquals(1, repository.count());
    }

}