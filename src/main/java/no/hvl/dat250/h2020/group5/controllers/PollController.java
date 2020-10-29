package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAuthority('USER')")
@RestController
@RequestMapping("/polls")
public class PollController {

  private final PollService pollService;
  private final ExtractIdFromAuth extractIdFromAuth;

  public PollController(PollService pollService, ExtractIdFromAuth extractIdFromAuth) {
    this.pollService = pollService;
    this.extractIdFromAuth = extractIdFromAuth;
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
    return pollService.createPoll(body, extractIdFromAuth.getIdFromAuth(authentication));
  }

  @DeleteMapping(path = "/{pollId}")
  public boolean deletePoll(@PathVariable Long pollId, Authentication authentication) {
    return pollService.deletePoll(pollId, extractIdFromAuth.getIdFromAuth(authentication));
  }

  @PreAuthorize("hasAuthority('USER') or hasAuthority('GUEST')")
  @GetMapping(path = "/{pollId}")
  public PollResponse getPoll(@PathVariable Long pollId) {
    return pollService.getPoll(pollId);
  }

  @PatchMapping(path = "/{pollId}")
  public boolean activatePoll(@PathVariable Long pollId, Authentication authentication) {
    return pollService.activatePoll(pollId, extractIdFromAuth.getIdFromAuth(authentication));
  }

  @GetMapping(path = "/{pollId}/active")
  public boolean isPollActive(@PathVariable Long pollId) {
    return pollService.isActivated(pollId);
  }

  @PreAuthorize("hasAuthority('USER') or hasAuthority('GUEST')")
  @GetMapping(path = "/{pollId}/votes")
  public VotesResponse getNumberOfVotes(@PathVariable Long pollId) {
    return pollService.getNumberOfVotes(pollId);
  }
}
