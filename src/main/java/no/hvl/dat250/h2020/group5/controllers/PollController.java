package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.security.services.UserDetailsImpl;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
public class PollController {

  private final PollService pollService;

  public PollController(PollService pollService) {
    this.pollService = pollService;
  }

  @GetMapping
  public List<PollResponse> getAllPublicPolls() {
    return pollService.getAllPublicPolls();
  }

  @PreAuthorize("authentication.principal.id == #ownerId or hasAuthority('ADMIN')")
  @GetMapping("owner/{ownerId}")
  public List<PollResponse> getAllPollsAsOwner(@PathVariable Long ownerId) {
    return pollService.getUserPollsAsOwner(ownerId);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/admin")
  public List<PollResponse> getAllPolls() {
    return pollService.getAllPolls();
  }

  @PostMapping
  public PollResponse createPoll(@RequestBody Poll body, Authentication authentication) {
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
    return pollService.createPoll(body, principal.getId());
  }

  @PreAuthorize("authentication.principal.id == #ownerId or hasAuthority('ADMIN')")
  @DeleteMapping(path = "/{pollId}/{ownerId}")
  public boolean deletePoll(@PathVariable Long pollId, @PathVariable Long ownerId) {
    return pollService.deletePoll(pollId, ownerId);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping(path = "/{pollId}")
  public PollResponse getPoll(@PathVariable Long pollId) {
    return pollService.getPoll(pollId);
  }

  @PatchMapping(path = "/{pollId}")
  public boolean activatePoll(@PathVariable Long pollId) {
    return pollService.activatePoll(pollId);
  }

  @GetMapping(path = "/{pollId}/active")
  public boolean isPollActive(@PathVariable Long pollId) {
    return pollService.isActivated(pollId);
  }

  @GetMapping(path = "/{pollId}/votes")
  public VotesResponse getNumberOfVotes(@PathVariable Long pollId) {
    return pollService.getNumberOfVotes(pollId);
  }
}
