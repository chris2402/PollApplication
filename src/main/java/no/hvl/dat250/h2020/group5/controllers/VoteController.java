package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.security.services.UserDetailsImpl;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/votes")
public class VoteController {

  final VoteService voteService;

  public VoteController(VoteService voteService) {
    this.voteService = voteService;
  }

  @PreAuthorize("hasAuthority('USER') or hasAuthority('GUEST')")
  @PostMapping("/{pollId}")
  public Vote castVote(
      @PathVariable Long pollId,
      Authentication authentication,
      @Valid @RequestBody CastVoteRequest castVoteRequest) {
    return voteService.vote(pollId, getIdFromAuth(authentication), castVoteRequest);
  }

  @PreAuthorize("authentication.principal.id = #userId or hasAuthority('ADMIN')")
  @GetMapping
  public Vote findVote(@RequestParam Long userId, @RequestParam Long pollId) {
    return voteService.findVote(pollId, userId);
  }

  public long getIdFromAuth(Authentication authentication) {
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
    return principal.getId();
  }
}
