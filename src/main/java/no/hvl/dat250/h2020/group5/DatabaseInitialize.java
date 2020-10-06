package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.Instant;

//@Component
public class DatabaseInitialize {

    @Autowired
    EntityManager em;

    private User user;
    private User user2;

    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final PollRepository pollRepository;

    public DatabaseInitialize(GuestRepository guestRepository, PollRepository pollRepository,
                              UserRepository userRepository) {
        this.userRepository = userRepository;
        this.guestRepository = guestRepository;
        this.pollRepository = pollRepository;
    }

    //    @PostConstruct
    public void setupUser(){
        this.user = new User();
        this.user.setId(1L);
        this.user.setIsAdmin(true);
        this.user.setUsername("Bob");
        this.user.setPassword("Bob1");

        this.user2 = new User();
        this.user2.setId(2L);
        this.user2.setIsAdmin(true);
        this.user2.setUsername("Bob");
        this.user2.setPassword("Bob1");

        this.user = userRepository.save(this.user);
        this.user2 = userRepository.saveAndFlush(this.user2);

    }

    //    @PostConstruct
    public void setupGuest(){
        Guest guest = new Guest();
        guest.setUsername("Guest 100");
        guest.setId(100L);

        Guest guest2 = new Guest();
        guest2.setUsername("Guest 106");
        guest2.setId(106L);

        guestRepository.save(guest);
        guestRepository.saveAndFlush(guest2);
    }

    //    @PostConstruct
    public void setupPoll(){
        Poll poll = new Poll();
        poll.setName("pinapple");
        poll.setQuestion("pinapple on pizza?");
        poll.setVisibilityType(PollVisibilityType.PUBLIC);
        poll.setOwnerAndAddThisPollToOwner(user);
        poll.setPollDuration(10000);
        poll.setStartTime(Date.from(Instant.now()));
        poll.setId(1679616L);

        Poll poll2 = new Poll();
        poll2.setName("Cats vs dogs");
        poll2.setQuestion("Cats better than dogs?");
        poll2.setVisibilityType(PollVisibilityType.PRIVATE);
        poll2.setOwnerAndAddThisPollToOwner(user2);
        poll2.setPollDuration(1000);
        poll2.setStartTime(Date.from(Instant.parse("2018-11-30T18:35:24Z")));
        poll2.setId(1679617L);

        Poll poll3 = new Poll();
        poll3.setName("Vue > React");
        poll3.setQuestion("Vue > React");
        poll3.setVisibilityType(PollVisibilityType.PUBLIC);
        poll3.setOwnerAndAddThisPollToOwner(user);
        poll3.setPollDuration(1000);
        poll3.setStartTime(Date.from(Instant.now()));
        poll3.setId(1679618L);

        pollRepository.saveAndFlush(poll);
        pollRepository.saveAndFlush(poll2);
        pollRepository.saveAndFlush(poll3);

    }
}
