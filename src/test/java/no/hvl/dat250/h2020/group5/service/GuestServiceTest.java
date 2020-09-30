package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class GuestServiceTest {

    @InjectMocks GuestService guestService;

    @Mock GuestRepository guestRepository;

    @Test
    public void shouldReturnAListOfAllGuestsTest() {
        List<Guest> guests = Arrays.asList(new Guest(), new Guest(), new Guest());
        when(guestRepository.findAll()).thenReturn(guests);
        Assertions.assertEquals(guests, guestService.getAllGuests());
    }

    @Test
    public void shouldSaveANewGuestTest() {
        Guest newGuest = new Guest();
        guestService.createGuest(newGuest);
        verify(guestRepository, times(1)).save(newGuest);
    }
}
