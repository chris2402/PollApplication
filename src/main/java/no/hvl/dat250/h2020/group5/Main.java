package no.hvl.dat250.h2020.group5;


import no.hvl.dat250.h2020.group5.dao.UserDAO;
import no.hvl.dat250.h2020.group5.entities.AnswerType;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
    private static final String PERSISTENCE_UNIT_NAME = "polls";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        TestClass testClass = new TestClass();
        testClass.setup();
        testClass.createGuest();
        testClass.createuser();
        testClass.getAllUsers();
        testClass.getUser();
        testClass.updateUserName();
        testClass.updatePassword();
        testClass.createPoll();
        testClass.getAllPublicPolls();
        testClass.getOwnPolls();
        testClass.getPoll();
        testClass.changePollStatus();
        testClass.getOwnPolls2();
        testClass.vote();
        testClass.changeVote();
//        testClass.deleteVote();
//        testClass.deletePoll();
//        testClass.deleteUser();

    }
}
