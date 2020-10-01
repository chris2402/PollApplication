package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.repositories.GuestRepository;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.repositories.VoterRepository;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private Guest guest1;
    private Guest guest2;
    private Guest guest3;

    @BeforeEach
    public void setup() {

        this.guest1 = new Guest();
        this.guest1.setId(1L);
        this.guest1.setUsername("Guest 1");

        this.guest2 = new Guest();
        this.guest2.setId(2L);
        this.guest2.setUsername("Guest 2");

        this.guest3 = new Guest();
        this.guest3.setId(3L);
        this.guest3.setUsername("Guest 3");

        when(guestRepository.save(guest1)).thenReturn(guest1);
        when(guestRepository.save(guest2)).thenReturn(guest2);
        when(guestRepository.save(guest3)).thenReturn(guest3);
    }

    @Test
    public void shouldReturnAListOfAllGuestsTest() {
        List<Guest> guests = Arrays.asList(guest1, guest2, guest3);
        when(guestRepository.findAll()).thenReturn(guests);
        List<GuestResponse> guestResponses =
                Arrays.asList(
                        new GuestResponse(guest1),
                        new GuestResponse(guest2),
                        new GuestResponse(guest3));
        int i = 0;
        for (Guest guest : guests) {
            Assertions.assertEquals(guest.getUsername(), guestResponses.get(i).getUsername());
            i++;
        }
    }

    @Test
    public void shouldSaveANewGuestTest() {
        guestService.createGuest(guest1);
        verify(guestRepository, times(1)).save(guest1);
    }
}
