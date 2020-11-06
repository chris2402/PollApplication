package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.VoteRequest;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/votes")
public class VoteController {

  final VoteService voteService;
  final ExtractFromAuth extractFromAuth;

  public VoteController(VoteService voteService, ExtractFromAuth extractFromAuth) {
    this.voteService = voteService;
    this.extractFromAuth = extractFromAuth;
  }

  @PostMapping("/{pollId}")
  public Vote castVote(
      @PathVariable Long pollId,
      Authentication authentication,
      @Valid @RequestBody VoteRequest voteRequest) {
    UUID userId =
        voteRequest.getId() == null ? extractFromAuth.userId(authentication) : voteRequest.getId();
    return voteService.vote(pollId, userId, voteRequest);
  }

  @PreAuthorize("authentication.principal.id = #userId or hasAuthority('ADMIN')")
  @GetMapping
  public Vote findVote(@RequestParam UUID userId, @RequestParam Long pollId) {
    return voteService.findVote(pollId, userId);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
