package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/votes")
public class VoteController {

  final VoteService voteService;
  final ExtractIdFromAuth extractIdFromAuth;

  public VoteController(VoteService voteService, ExtractIdFromAuth extractIdFromAuth) {
    this.voteService = voteService;
    this.extractIdFromAuth = extractIdFromAuth;
  }

  @PreAuthorize("hasAuthority('USER') or hasAuthority('GUEST')")
  @PostMapping("/{pollId}")
  public Vote castVote(
      @PathVariable Long pollId,
      Authentication authentication,
      @Valid @RequestBody CastVoteRequest castVoteRequest) {
    return voteService.vote(
        pollId, extractIdFromAuth.getIdFromAuth(authentication), castVoteRequest);
  }

  @PreAuthorize("authentication.principal.id = #userId or hasAuthority('ADMIN')")
  @GetMapping
  public Vote findVote(@RequestParam Long userId, @RequestParam Long pollId) {
    return voteService.findVote(pollId, userId);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
