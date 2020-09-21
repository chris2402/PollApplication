package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GuestService  {

    @Autowired
    private GuestRepository guestRepository;

    public Guest createGuest() {
        Guest guest = new Guest();
        guest.setUserName("Guest " + guest.getId());
        return guestRepository.save(guest);
    }

}
