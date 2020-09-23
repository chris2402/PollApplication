package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class GuestService  {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public List<Guest> getAllGuest(){
        return guestRepository.findAll();
    }

    public Guest createGuest(@RequestBody Guest guest) {
        return guestRepository.save(guest);
    }

}
