package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.requests.CreateOrUpdatePollRequest;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
public class PollController {

  private final PollService pollService;
  private final ExtractFromAuth extractFromAuth;

  public PollController(PollService pollService, ExtractFromAuth extractFromAuth) {
    this.pollService = pollService;
    this.extractFromAuth = extractFromAuth;
  }

  @GetMapping
  public List<PollResponse> getAllPublicPolls() {
    return pollService.getAllPublicPolls();
  }

  @PreAuthorize("authentication.principal.id == #ownerId or hasAuthority('ADMIN')")
  @GetMapping("owner/{ownerId}")
  public List<PollResponse> getAllPollsAsOwner(@PathVariable UUID ownerId) {
    return pollService.getUserPollsAsOwner(ownerId);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/admin")
  public List<PollResponse> getAllPolls() {
    return pollService.getAllPolls();
  }

  @PreAuthorize("hasAuthority('USER')")
  @PostMapping
  public PollResponse createPoll(
      @RequestBody CreateOrUpdatePollRequest createOrUpdatePollRequest,
      Authentication authentication) {
    return pollService.createPoll(
        createOrUpdatePollRequest, extractFromAuth.userId(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @PutMapping(path = "/{pollId}")
  public PollResponse updatePoll(
      @PathVariable Long pollId,
      @RequestBody CreateOrUpdatePollRequest request,
      Authentication authentication) {
    return pollService.updatePoll(pollId, request, extractFromAuth.userId(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @DeleteMapping(path = "/{pollId}")
  public boolean deletePoll(@PathVariable Long pollId, Authentication authentication) {
    return pollService.deletePoll(pollId, extractFromAuth.userId(authentication));
  }

  @GetMapping(path = "/{pollId}")
  public PollResponse getPoll(@PathVariable Long pollId, Authentication authentication) {
    return pollService.getPoll(
        pollId, authentication == null ? null : extractFromAuth.userId(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @PatchMapping(path = "/{pollId}")
  public boolean activatePoll(@PathVariable Long pollId, Authentication authentication) {
    return pollService.activatePoll(pollId, extractFromAuth.userId(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @GetMapping(path = "/{pollId}/active")
  public boolean isPollActive(@PathVariable Long pollId) {
    return pollService.isActivated(pollId);
  }

  @GetMapping(path = "/{pollId}/votes")
  public VotesResponse getNumberOfVotes(@PathVariable Long pollId, Authentication authentication) {
    return pollService.getNumberOfVotes(
        pollId, authentication == null ? null : extractFromAuth.userId(authentication));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
