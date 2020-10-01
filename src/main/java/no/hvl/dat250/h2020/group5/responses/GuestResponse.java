package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Guest;

@Getter
@Setter
@AllArgsConstructor
public class GuestResponse {

    private Long id;
    private String username;


    public GuestResponse(Guest guest) {
        this.id = guest.getId();
        this.username = guest.getUsername();
    }
}
