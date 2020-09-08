package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.doa.GuestDOA;
import no.hvl.dat250.h2020.group5.entities.Guest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Random;

public class GuestService implements GuestDOA {

    @PersistenceContext
    private EntityManager em;

    private Random random = new Random();

    @Override
    public Guest createGuest() {
        Guest guest = new Guest();
        String id;

        do{
            id = Integer.toString(getRandomIntInRange(1000000, 10000));

        }while(em.find(Guest.class, id) != null);

        guest.setId(id);
        guest.setUserName("Guest" + id);

        em.persist(guest);
        em.getTransaction().commit();

        return guest;
    }

    private int getRandomIntInRange(int high, int low){
        return random.nextInt(high-low) + low;
    }
}
