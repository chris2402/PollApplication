package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.responses.GuestResponse;
import no.hvl.dat250.h2020.group5.service.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {

  private final GuestService guestService;

  public GuestController(GuestService guestService) {
    this.guestService = guestService;
  }

  @PostMapping("/signup")
  public GuestResponse createGuest(@RequestBody Guest guest, HttpServletResponse response) {
    return guestService.createGuest(guest);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<GuestResponse> getAllGuests() {
    return guestService.getAllGuests();
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
