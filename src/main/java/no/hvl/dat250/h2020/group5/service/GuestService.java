package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public List<GuestResponse> getAllGuests() {
        List<GuestResponse> guestResponseList = new ArrayList<>();
        guestRepository.findAll().forEach(guest -> guestResponseList.add(new GuestResponse(guest)));
        return guestResponseList;
    }

    public GuestResponse createGuest(Guest guest) {
        return new GuestResponse(guestRepository.save(guest));
    }
}
