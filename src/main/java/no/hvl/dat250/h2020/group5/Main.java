package no.hvl.dat250.h2020.group5;


import no.hvl.dat250.h2020.group5.dao.UserDAO;
import no.hvl.dat250.h2020.group5.enums.AnswerType;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.IOException;

public class Main {
    private static final String PERSISTENCE_UNIT_NAME = "polls";
    private static EntityManagerFactory factory;

    public static void main(String[] args) throws IOException {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        TestClass testClass = new TestClass();
        testClass.setup();
        testClass.createGuest("123");
        testClass.createUser("Hans", "Postkasse2");
        testClass.createUser("Bob", "Bob1");
        testClass.getAllUsers();
        testClass.getAllVoters();
        System.in.read();
        testClass.getUser();
        System.in.read();
        testClass.updateUserName();
        testClass.updatePassword();
        System.in.read();
        testClass.createPoll();
        System.in.read();
        testClass.getAllPublicPolls();
//        testClass.getOwnPolls();
        testClass.getPoll();
        System.in.read();
        testClass.changePollStatus();
        testClass.getOwnPolls2();
        System.in.read();
        testClass.vote();
        testClass.changeVote();
        System.in.read();
        testClass.deleteVote();
        testClass.deletePoll();
        testClass.deleteUser();

    }
}
