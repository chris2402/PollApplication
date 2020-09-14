package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.converters.AlphaNumeric2Long;
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

    public void createGuest(String id){
        guestService.createGuest(id);
        Guest guest = (Guest) em.createQuery("select g from Guest g").getResultList().get(0);
        userId = guest.getId();
        System.out.println("----- CREATE GUEST -----");
        System.out.println(guest.getUserName());
    }

    public void createUser(String name, String pass){
        User user = userService.createUser(name, pass);
        userId = user.getId();
        System.out.println("----- CREATE USER -----");
        System.out.println(em.find(Voter.class, userId).getUserName());
    }

    public void getAllUsers(){
        List<User> users = userService.getAllUsers();
        System.out.println("----- GET ALL USERS -----");
        for(User user : users){
            System.out.println(user.getUserName());
        }
    }

    public void getAllVoters(){
        System.out.println("----- GET ALL Voters -----");
        List<Voter> voters = em.createQuery("select v from Voter v").getResultList();
        for(Voter v : voters){
            System.out.println(v.getUserName());
        }
    }

    public void getUser(){
        User user = userService.getUser(userId);
        System.out.println("----- GET USER -----");
        System.out.println(user.getUserName());
    }

    public void updateUserName(){
        boolean changedName = userService.updateUsername(userId, "KåreJ25");
        em.clear();
        System.out.println("----- CHANGE USERNAME -----");
        System.out.println(changedName);
        System.out.println(em.find(User.class, userId).getUserName());
    }

    public void updatePassword(){
        boolean changedPassword = userService.updatePassword(userId, "Bob1", "MobilLadder27");
        System.out.println("---- CHANGE PASSWORD -----");
        em.clear();
        User user = (User) em.createQuery("select u from User u").getResultList().get(0);
        System.out.println(user.getPassword());
        System.out.println(changedPassword);
    }

    public void createPoll(){
        Poll poll = pollService.createPoll("Annanas", "Annanas on pizza?",
                userId, 20, true);
        pollId = poll.getId();
        System.out.println("---- CREATE POLL -----");
        System.out.println(poll.getName());
        System.out.println(poll.getId());
    }

    public void getAllPublicPolls(){
        List<Poll> publicPolls = pollService.getAllPublicPolls();
        for(Poll poll : publicPolls){
            System.out.println("---- GET ALL PUBLIC POLLS ----");
            System.out.println(poll.getName());
        }
    }

    public void getOwnPolls(){
        System.out.println("Get own polls, wrong version");
        List<Poll> publicPolls = pollService.getOwnPolls("1234");
        for(Poll poll : publicPolls){
            System.out.println("---- GET OWN POLLS (NO POLLS) ----");
            System.out.println(poll.getName());
        }
    }

    public void getOwnPolls2(){
        List<Poll> publicPolls = pollService.getOwnPolls(userId);
        for(Poll poll : publicPolls){
            System.out.println("---- GET OWN POLLS ----");
            System.out.print("Poll name: ");
            System.out.println(poll.getName());
            System.out.print("Poll id: ");
            System.out.println(poll.getId());
            System.out.print("Poll owner: ");
            System.out.println(poll.getPollOwner().getUserName());
        }
    }

    public void getPoll(){
        Poll poll = pollService.getPoll(pollId);
        System.out.println("---- GET POLL -----");
        System.out.println(poll.getName());
        System.out.println(poll.getId());
    }

    public void changePollStatus(){
        System.out.println(em.find(Poll.class, pollId).getActive());
        pollService.changePollStatus(pollId, true);
        System.out.println("----- CHANGE POLL STATUS -----");
        em.clear();
        System.out.println(em.find(Poll.class, pollId).getActive());

    }

    public void vote(){
        voteService.vote(pollId, userId, "yes");
        System.out.println("----- VOTE -----");
        em.clear();
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
    }

    public void changeVote(){
        voteService.changeVote(pollId, userId, "no");
        System.out.println("----- CHANGE VOTE -----");
        em.clear();
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
    }

    public void deleteVote(){
        voteService.deleteVote(pollId, userId);
        System.out.println("----- DELETE VOTE -----");
        Query q = em.createQuery("select v from Vote v where v.voter = :voter and v.poll = :poll");
        q.setParameter("voter", em.find(Voter.class, userId));
        q.setParameter("poll", em.find(Poll.class, pollId));
        System.out.println(q.getResultList().size());
    }

    public void deletePoll(){
        pollService.deletePoll(pollId);
        System.out.println("----- DELETE POLL -----");
        System.out.println(em.createQuery("select p from Poll p").getResultList().size());
    }

    public void deleteUser(){
        userService.deleteUser(userId);
        System.out.println("----- DELETE USER -----");
        System.out.println(em.createQuery("select u from User u").getResultList().size());
    }


}
