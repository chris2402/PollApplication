package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Voter;
import no.hvl.dat250.h2020.group5.service.GuestService;
import no.hvl.dat250.h2020.group5.service.PollService;
import no.hvl.dat250.h2020.group5.service.UserService;
import no.hvl.dat250.h2020.group5.service.VoteService;

import javax.persistence.*;
import java.util.List;

public class TestClass {

    private static final String PERSISTENCE_UNIT_NAME = "polls";
    private static EntityManagerFactory factory;

    @PersistenceContext
    EntityManager em;


    GuestService guestService = new GuestService();
    UserService userService = new UserService();
    PollService pollService = new PollService();
    VoteService voteService = new VoteService();

    String userId = "";
    String pollId = "";

    public void setup(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
        guestService.setup();
        pollService.setup();
        userService.setup();
        voteService.setup();
    }

    public void createGuest(){
        guestService.createGuest();
        Guest guest = (Guest) em.createQuery("select g from Guest g").getResultList().get(0);
        userId = guest.getId();
        System.out.println("-----------------Create guest---------------------------");
        System.out.println(em.createQuery("select g from Guest g").getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void createuser(){
        User user = userService.createUser("Bob", "Bob1");
        userId = user.getId();
        System.out.println("---------------------create user-----------------------");
        System.out.println(em.createQuery("select u from User u").getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void getAllUsers(){
        List<User> users = userService.getAllUsers();
        for(User user : users){
            System.out.println("----------------------get all users----------------------");
            System.out.println(user.getUserName());
            System.out.println("--------------------------------------------");
        }
    }

    public void getUser(){
        User user = userService.getUser(userId);
        System.out.println("----------------------------get user ----------------");
        System.out.println(user.getUserName());
        System.out.println("--------------------------------------------");
    }

    public void updateUserName(){
        boolean changedName = userService.updateUsername(userId, "Kåre");
        System.out.println("------------------------update username--------------------");
        System.out.println(changedName);
        System.out.println(em.find(User.class, userId).getUserName());
        System.out.println("--------------------------------------------");
    }

    public void updatePassword(){
        boolean changedPassword = userService.updatePassword(userId, "Bob1", "BigPP");
        System.out.println("----------------------update password----------------------");
        User user = (User) em.createQuery("select u from User u").getResultList().get(0);
        System.out.println(user.getPassword());
        System.out.println(changedPassword);
        System.out.println("--------------------------------------------");
    }

    public void createPoll(){
        Poll poll = pollService.createPoll("Annanas", "Annanas on pizza?", userId, 20, true);
        pollId = poll.getId();
        System.out.println("---------------------create poll-----------------------");
        System.out.println(poll.getName());
        System.out.println("-------------------------------------------");
    }

    public void getAllPublicPolls(){
        List<Poll> publicPolls = pollService.getAllPublicPolls();
        for(Poll poll : publicPolls){
            System.out.println("---------------------------get all public polls-----------------");
            System.out.println(poll.getName());
            System.out.println("--------------------------------------------");
        }
    }

    public void getOwnPolls(){
        System.out.println("Get own polls, wrong version");
        List<Poll> publicPolls = pollService.getOwnPolls("1234");
        for(Poll poll : publicPolls){
            System.out.println("---------------------get own polls 1-----------------------");
            System.out.println(poll.getName());
            System.out.println("--------------------------------------------");
        }
    }

    public void getOwnPolls2(){
        List<Poll> publicPolls = pollService.getOwnPolls(userId);
        for(Poll poll : publicPolls){
            System.out.println("--------------------get own pols 2------------------------");
            System.out.println(poll.getName());
            System.out.println("--------------------------------------------");
        }
    }

    public void getPoll(){
        Poll poll = pollService.getPoll(pollId);
        System.out.println("--------------------------------------------");
        System.out.println(poll.getName());
        System.out.println(poll.getId());
        System.out.println("--------------------------------------------");
    }

    public void changePollStatus(){
        System.out.println(em.find(Poll.class, pollId).getActive());
        pollService.changePollStatus(pollId, true);
        System.out.println("----------------------change poll status----------------------");
        System.out.println(em.find(Poll.class, pollId).getActive());
        System.out.println("--------------------------------------------");
    }

    public void vote(){
        voteService.vote(pollId, userId, "yes");
        System.out.println("----------------- vote ---------------------------");
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void changeVote(){
        voteService.changeVote(pollId, userId, "no");
        System.out.println("-------------------- change vote ------------------------");
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void deleteVote(){
        voteService.deleteVote(pollId, userId);
        System.out.println("--------------------- delete vote -----------------------");
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void deletePoll(){
        pollService.deletePoll(pollId);
        System.out.println("------------------- delete poll -------------------------");
        System.out.println(em.createQuery("select p from Poll p").getResultList().size());
        System.out.println("--------------------------------------------");
    }

    public void deleteUser(){
        userService.deleteUser(userId);
        System.out.println("--------------------- delete user -----------------------");
        System.out.println(em.createQuery("select u from User u").getResultList().size());
        System.out.println("--------------------------------------------");
    }


}
