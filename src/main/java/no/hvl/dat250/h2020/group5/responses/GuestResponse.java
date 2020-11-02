package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Guest;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GuestResponse {

  private UUID id;
  private String displayName;

  public GuestResponse(Guest guest) {
    this.id = guest.getId();
    this.displayName = guest.getDisplayName();
  }
}
