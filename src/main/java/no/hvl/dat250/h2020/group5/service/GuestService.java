package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.GuestDAO;
import no.hvl.dat250.h2020.group5.entities.Guest;

import javax.persistence.*;

import java.util.Random;

public class GuestService implements GuestDAO {


    private static final String PERSISTENCE_UNIT_NAME = "polls";
    private static EntityManagerFactory factory;

    @PersistenceContext
    EntityManager em;

    private Random random = new Random();

    @Override
    public Guest createGuest() {
        Guest guest = new Guest();
        em.getTransaction().begin();
//        String id;
//
//        do{
//            id = Integer.toString(getRandomIntInRange(1000000, 10000));
//
//        } while(em.find(Guest.class, id) != null);
//
//        guest.setId(id);
        guest.setId("123");
        guest.setUserName("Guest" + 123);

        em.persist(guest);
        em.getTransaction().commit();

        return guest;
    }

    private int getRandomIntInRange(int high, int low){
        return random.nextInt(high-low) + low;
    }

    public void setup(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
}
