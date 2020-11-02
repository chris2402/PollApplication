package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.CreateCookie;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

  private final GuestService guestService;
  private final CreateCookie createCookie;

  public GuestController(GuestService guestService, CreateCookie createCookie) {
    this.guestService = guestService;
    this.createCookie = createCookie;
  }

  @PostMapping("/signup")
  public GuestResponse createGuest(@RequestBody Guest guest, HttpServletResponse response) {
    GuestResponse savedGuest = guestService.createGuest(guest);
    createCookie.createGuestCookie(savedGuest.getId(), response);
    return savedGuest;
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<GuestResponse> getAllGuests() {
    return guestService.getAllGuests();
  }
}
