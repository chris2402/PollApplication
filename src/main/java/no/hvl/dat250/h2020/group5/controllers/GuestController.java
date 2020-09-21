package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/")
    public List<Guest> getAllGuests(){
        return guestService.getAllGuest();
    }

    @PostMapping("/")
    public Guest createGuest(@RequestBody Guest guest){
        return guestService.createGuest(guest);
    }


}
