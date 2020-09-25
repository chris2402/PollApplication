//import no.hvl.dat250.h2020.group5.enums.AnswerType;
//import no.hvl.dat250.h2020.group5.entities.Guest;
//import no.hvl.dat250.h2020.group5.entities.Poll;
//import no.hvl.dat250.h2020.group5.entities.User;
//import no.hvl.dat250.h2020.group5.entities.Vote;
//import org.junit.jupiter.api.*;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.persistence.*;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//@SpringBootTest
//public class JPATest {
//
//    private static final String PERSISTENCE_UNIT_NAME = "test-polls";
//    private static EntityManagerFactory factory;
//    private EntityManager em;
//
//    @BeforeEach
//    public void setUpEMF() throws Exception{
//        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//        em = factory.createEntityManager();
//        em.getTransaction().begin();
//
//        Query q1 = em.createQuery("DELETE FROM Vote");
//        Query q2 = em.createQuery("DELETE FROM Voter");
//        Query q3 = em.createQuery("DELETE FROM Guest");
//        Query q4 = em.createQuery("DELETE FROM User");
//        Query q5 = em.createQuery("DELETE FROM Poll");
//        Query q6 = em.createQuery("DELETE FROM VotingDevice");
//        Query q7 = em.createQuery("DELETE FROM DisplayDevice");
//
//        q1.executeUpdate();
//        q2.executeUpdate();
//        q3.executeUpdate();
//        q4.executeUpdate();
//        q5.executeUpdate();
//        q6.executeUpdate();
//        q7.executeUpdate();
//
//        em.getTransaction().commit();
//    }
//
//
//    @AfterEach
//    public void tearDownEM(){
//        em.close();
//    }
//
//
//    @Test
//    public void testInsertGuest(){
//        Guest g = new Guest();
//        g.setUsername("IM A USER");
//
//        EntityTransaction trans = em.getTransaction();
//        trans.begin();
//        em.persist(g);
//        em.getTransaction().commit();
//
//        Query q = em.createQuery("select g from Guest g");
//        List<Guest> resultList = q.getResultList();
//
//        Assertions.assertEquals(resultList.size(), 1);
//        Assertions.assertEquals(resultList.get(0).getId(), g.getId());
//    }
//
//    @Test
//    public void shouldPersistYesWhenVotingYesTest() {
//        Vote vote = new Vote();
//        vote.setAnswer(AnswerType.YES);
//
//        Poll poll = new Poll();
//        em.getTransaction().begin();
//        em.persist(poll);
//        em.getTransaction().commit();
//        vote.setPoll(poll);
//
//        Guest voter = new Guest();
//        // Many-side is the owning side, vote persists voter
//        vote.setVoter(voter);
//
//        em.getTransaction().begin();
//        em.persist(vote);
//        em.getTransaction().commit();
//
//        Query query = em.createQuery("select v from Vote v");
//        List<Vote> votes = query.getResultList();
//
//        Assertions.assertEquals(AnswerType.YES, votes.get(0).getAnswer());
//    }
//
//    @Test
//    public void shouldCountTwoVotesWhenGivenOnPollTest() {
//        Poll poll = new Poll();
//
//        Guest voter1 = new Guest();
//
//        Guest voter2 = new Guest();
//
//        Vote vote1 = new Vote();
//        vote1.setVoter(voter1);
//        vote1.setAnswer(AnswerType.YES);
//
//        Vote vote2 = new Vote();
//        vote2.setVoter(voter2);
//        vote2.setAnswer(AnswerType.YES);
//
//        // Many-side is the owning side, vote persists poll
//        vote1.setPoll(poll);
//        vote2.setPoll(poll);
//        poll.setVotes(Arrays.asList(vote1, vote2));
//
//        em.getTransaction().begin();
//        em.persist(vote1);
//        em.persist(vote2);
//        em.getTransaction().commit();
//
//        Query query = em.createQuery("select p from Poll p");
//        List<Poll> polls = query.getResultList();
//
//        Assertions.assertEquals(2, polls.get(0).getVotes().size());
//    }
//
////    @Test
////    public void PollIdStringConvertPersistAndFindTest() {
////        final String POLL_ID = "ABC123";
////        Poll poll = new Poll();
////        poll.setId(POLL_ID);
////
////        em.getTransaction().begin();
////        em.persist(poll);
////        em.getTransaction().commit();
////
////        Poll fetched_poll = em.find(Poll.class, POLL_ID);
////
////        Assertions.assertEquals(fetched_poll.getId(), POLL_ID);
////    }
//
//    @Test
//    public void shouldKeepVoteAndChangeFkWhenVoterIsDeleted(){
//        Poll poll = new Poll();
//
//        Guest voter1 = new Guest();
//
//        Vote vote1 = new Vote();
//        vote1.setVoter(voter1);
//        vote1.setPoll(poll);
//        vote1.setAnswer(AnswerType.YES);
//
//        em.getTransaction().begin();
//        em.persist(vote1);
//        em.getTransaction().commit();
//
//        em.getTransaction().begin();
//        List<Vote> votes = em.createQuery("select v from Vote v where v.voter.id = :voteid AND v.poll.id = :pollid")
//                .setParameter("pollid", voter1.getId())
//                .setParameter("voteid", poll.getId())
//                .getResultList();
//        for(Vote vote : votes){
//            vote.setVoter(null);
//        }
//        em.remove(voter1);
//        em.getTransaction().commit();
//
//        List<Vote> updatedVotes = em.createQuery("select v from Vote v")
//                .getResultList();
//
//        List<Vote> voters = em.createQuery("select v from Voter v")
//                .getResultList();
//
//        Assertions.assertNull(updatedVotes.get(0).getVoter());
//        Assertions.assertEquals(poll,updatedVotes.get(0).getPoll());
//        Assertions.assertEquals(0,voters.size());
//    }
//
//    @Test
//    public void shouldDeletePollOwnedByUserWhenDeletingUserTest() {
//        User user = new User();
//        user.setUsername("User1");
//        user.setPassword("MyPassword");
//
//        Poll poll = new Poll();
//
//        user.setUserPolls(Collections.singletonList(poll));
//        poll.setPollOwner(user);
//
//        em.getTransaction().begin();
//        em.persist(poll);
//        em.getTransaction().commit();
//
//        Query queryBeforeDeletion = em.createQuery("select p from Poll p");
//        List<Poll> pollsBeforeDeletion = queryBeforeDeletion.getResultList();
//
//        em.getTransaction().begin();
//        em.remove(user);
//        em.getTransaction().commit();
//
//        Query queryAfterDeletion = em.createQuery("select p from Poll p");
//        List<Poll> pollsAfterDeletion = queryAfterDeletion.getResultList();
//
//        Assertions.assertEquals(1, pollsBeforeDeletion.size());
//        Assertions.assertEquals(0, pollsAfterDeletion.size());
//    }
//
//    @Test
//    public void shouldDeleteVoteWhenPollIsDeletedTest() {
//        Poll poll = new Poll();
//
//        Vote vote = new Vote();
//        vote.setAnswer(AnswerType.NO);
//        vote.setPoll(poll);
//
//        Guest voter = new Guest();
//
//        vote.setVoter(voter);
//        poll.setVotes(Collections.singletonList(vote));
//
//        em.getTransaction().begin();
//        em.persist(poll);
//        em.getTransaction().commit();
//
//        Query queryBeforeDeletion = em.createQuery("select v from Vote v");
//        List<Vote> votesBeforeDeletion = queryBeforeDeletion.getResultList();
//
//        em.getTransaction().begin();
//        em.remove(poll);
//        em.getTransaction().commit();
//
//        Query queryAfterDeletion = em.createQuery("select v from Vote v");
//        List<Vote> votesAfterDeletion = queryAfterDeletion.getResultList();
//
//        Assertions.assertEquals(1, votesBeforeDeletion.size());
//        Assertions.assertEquals(0, votesAfterDeletion.size());
//
//    }
//
//}
