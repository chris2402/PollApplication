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
    public static void setUpEMF() throws Exception{
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();

        Query q1 = em.createQuery("DELETE FROM Guest");
        Query q2 = em.createQuery("DELETE FROM User");
        Query q3 = em.createQuery("DELETE FROM Voter");
        Query q4 = em.createQuery("DELETE FROM Vote");
        Query q5 = em.createQuery("DELETE FROM Poll");
        Query q7 = em.createQuery("DELETE FROM VotingDevice");
        Query q8 = em.createQuery("DELETE FROM DisplayDevice");

        q1.executeUpdate();
        q2.executeUpdate();
        q3.executeUpdate();
        q4.executeUpdate();
        q5.executeUpdate();
        q7.executeUpdate();
        q8.executeUpdate();

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
        vote.setAnswer(AnswerType.YES.toString());

        Poll poll = new Poll();
        poll.setId("1");
        em.persist(poll);
        em.getTransaction().commit();
        vote.setPoll(poll);

        Guest voter = new Guest();
        voter.setId("1");
        vote.setVoter(voter);

        em.getTransaction().begin();
        em.persist(vote);
        em.getTransaction().commit();

        Query query = em.createQuery("select v from Vote v");
        List<Vote> votes = query.getResultList();

        Assertions.assertEquals(AnswerType.YES.toString(), votes.get(0).getAnswer());
    }

    @Test
    public void shouldCountTwoYesVotesWhenGivenOnPollTest() {
        Poll poll = new Poll();
        poll.setId("1");
    }
}
