package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuestService  {

    @Autowired
    private GuestRepository guestRepository;


//    private static final String PERSISTENCE_UNIT_NAME = "polls";
//    private static EntityManagerFactory factory;
//
//    @PersistenceContext
//    EntityManager em;
//
//    private Random random = new Random();

    public Guest createGuest(String id) {
        Guest guest = new Guest();
        guest.setId(id);
        guest.setUserName("Guest " + id);
        return guestRepository.save(guest);


//
//        Guest guest = new Guest();
//        em.getTransaction().begin();
//        String id;
//
//        do{
//            id = Integer.toString(getRandomIntInRange(1000000, 10000));
//
//        } while(em.find(Guest.class, id) != null);
//
//        guest.setId(id);
//        guest.setId(id);
//        guest.setUserName("Guest " + id);
//
//        em.persist(guest);
//        em.getTransaction().commit();
//
//        return guest;
    }

//    private int getRandomIntInRange(int high, int low){
//        return random.nextInt(high-low) + low;
//    }
//
//    public void setup(){
//        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//        em = factory.createEntityManager();
//    }
}
