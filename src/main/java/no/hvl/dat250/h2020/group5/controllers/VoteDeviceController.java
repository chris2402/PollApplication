package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractFromAuth;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import no.hvl.dat250.h2020.group5.service.VoteService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/voting-device")
public class VoteDeviceController {

  final VoteService voteService;
  final VotingDeviceService votingDeviceService;
  final ExtractFromAuth extractFromAuth;

  public VoteDeviceController(
      VoteService voteService,
      VotingDeviceService votingDeviceService,
      ExtractFromAuth extractFromAuth) {
    this.voteService = voteService;
    this.votingDeviceService = votingDeviceService;
    this.extractFromAuth = extractFromAuth;
  }

  @PostMapping("/{pollId}")
  public List<Vote> vote(
      @PathVariable Long pollId, @RequestBody VoteRequestFromDevice voteRequestFromDevice) {
    return voteService.saveVotesFromDevice(pollId, voteRequestFromDevice);
  }

  @PreAuthorize("hasAuthority('USER')")
  @DeleteMapping("/{deviceId}")
  public boolean deleteDevice(@PathVariable UUID deviceId, Authentication authentication) {
    return votingDeviceService.deleteDevice(deviceId, extractFromAuth.userId(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @PostMapping
  public VotingDeviceResponse createDevice(
      @RequestBody VotingDevice votingDevice, Authentication authentication) {
    return votingDeviceService.addDeviceToUser(
        extractFromAuth.userId(authentication), votingDevice);
  }

  @PreAuthorize("hasAuthority('USER')")
  @GetMapping
  public List<VotingDeviceResponse> getAllUsersDevices(Authentication authentication) {
    return votingDeviceService.getAllDevicesToOwner(extractFromAuth.userId(authentication));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUserNotFoundException(Exception exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
