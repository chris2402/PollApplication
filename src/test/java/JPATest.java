import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.AnswerType;
import no.hvl.dat250.h2020.group5.entities.Voter;
import org.junit.jupiter.api.*;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JPATest {

    private static final String PERSISTENCE_UNIT_NAME = "test-polls";
    private static EntityManagerFactory factory;
    private EntityManager em;

    @BeforeEach
    public void setUpEMF() throws Exception{
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Query q1 = em.createQuery("DELETE FROM Vote");
        Query q2 = em.createQuery("DELETE FROM Voter");
        Query q3 = em.createQuery("DELETE FROM Guest");
        Query q4 = em.createQuery("DELETE FROM User");
        Query q5 = em.createQuery("DELETE FROM Poll");
        Query q6 = em.createQuery("DELETE FROM VotingDevice");
        Query q7 = em.createQuery("DELETE FROM DisplayDevice");

        q1.executeUpdate();
        q2.executeUpdate();
        q3.executeUpdate();
        q4.executeUpdate();
        q5.executeUpdate();
        q6.executeUpdate();
        q7.executeUpdate();

        em.getTransaction().commit();
        em.close();

    }

    @BeforeEach
    public void setUpEM(){
        em = factory.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    public void tearDownEM(){
        em.close();
    }


    @Test
    public void testInsertGuest(){
        Guest g = new Guest();
        g.setId("123");
        g.setUserName("IM A USER");
        em.persist(g);
        em.getTransaction().commit();

        Query q = em.createQuery("select g from Guest g");
        List<Guest> resultList = q.getResultList();

        Assertions.assertEquals(resultList.size(), 1);
        Assertions.assertEquals(resultList.get(0).getId(), "123");
    }

    @Test
    public void shouldPersistYesWhenVotingYesTest() {
        Vote vote = new Vote();
        vote.setAnswer(AnswerType.YES);

        Poll poll = new Poll();
        poll.setId("1");
        em.persist(poll);
        em.getTransaction().commit();
        vote.setPoll(poll);

        Guest voter = new Guest();
        voter.setId("1");
        // Many-side is the owning side, vote persists voter
        vote.setVoter(voter);

        em.getTransaction().begin();
        em.persist(vote);
        em.getTransaction().commit();

        Query query = em.createQuery("select v from Vote v");
        List<Vote> votes = query.getResultList();

        Assertions.assertEquals(AnswerType.YES, votes.get(0).getAnswer());
    }

    @Test
    public void shouldCountTwoVotesWhenGivenOnPollTest() {
        Poll poll = new Poll();
        poll.setId("1");

        Guest voter1 = new Guest();
        voter1.setId("1");

        Guest voter2 = new Guest();
        voter2.setId("2");

        Vote vote1 = new Vote();
        vote1.setVoter(voter1);
        vote1.setAnswer(AnswerType.YES);

        Vote vote2 = new Vote();
        vote2.setVoter(voter2);
        vote2.setAnswer(AnswerType.YES);

        // Many-side is the owning side, vote persists poll
        vote1.setPoll(poll);
        vote2.setPoll(poll);
        poll.setVotes(Arrays.asList(vote1, vote2));

        em.persist(vote1);
        em.persist(vote2);
        em.getTransaction().commit();

        Query query = em.createQuery("select p from Poll p");
        List<Poll> polls = query.getResultList();

        Assertions.assertEquals(2, polls.get(0).getVotes().size());
    }

    @Test
    public void shouldDeleteVoterWithoutDeletingVoteAndSetFkToNull(){
        Poll poll = new Poll();
        poll.setId("1");

        Guest voter1 = new Guest();
        voter1.setId("1");

        Vote vote1 = new Vote();
        vote1.setVoter(voter1);
        vote1.setAnswer(AnswerType.YES);

        em.persist(vote1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        List<Vote> votes = em.createQuery("select v from Vote v where v.voter.id = :id")
                        .setParameter("id", voter1.getId())
                        .getResultList();
        for(Vote vote : votes){
            vote.setVoter(null);
        }
        em.remove(voter1);
        em.getTransaction().commit();

        List<Vote> updatedVotes = em.createQuery("select v from Vote v")
                                    .getResultList();

        List<Vote> voters = em.createQuery("select v from Voter v")
                              .getResultList();

        Assertions.assertNull(updatedVotes.get(0).getVoter());
        Assertions.assertEquals(0,voters.size());
    }
}
