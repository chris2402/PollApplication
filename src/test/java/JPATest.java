import no.hvl.dat250.h2020.group5.entities.Guest;
import org.junit.jupiter.api.*;

import javax.persistence.*;
import java.util.List;

public class JPATest {

    private static final String PERSISTENCE_UNIT_NAME = "test-polls";
    private static EntityManagerFactory factory;
    private EntityManager em;

    @BeforeAll
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
}
