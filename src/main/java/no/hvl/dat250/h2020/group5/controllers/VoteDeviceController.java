package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.controllers.utils.ExtractIdFromAuth;
import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import no.hvl.dat250.h2020.group5.service.VoteService;
import no.hvl.dat250.h2020.group5.service.VotingDeviceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voting-device")
public class VoteDeviceController {

  final VoteService voteService;
  final VotingDeviceService votingDeviceService;
  final ExtractIdFromAuth extractIdFromAuth;

  public VoteDeviceController(
      VoteService voteService,
      VotingDeviceService votingDeviceService,
      ExtractIdFromAuth extractIdFromAuth) {
    this.voteService = voteService;
    this.votingDeviceService = votingDeviceService;
    this.extractIdFromAuth = extractIdFromAuth;
  }

  @PostMapping("/{pollId}")
  public List<Vote> vote(
      @PathVariable Long pollId, @RequestBody VoteRequestFromDevice voteRequestFromDevice) {
    return voteService.saveVotesFromDevice(pollId, voteRequestFromDevice);
  }

  @PreAuthorize("hasAuthority('USER')")
  @DeleteMapping("/{deviceId}")
  public boolean deleteDevice(@PathVariable Long deviceId, Authentication authentication) {
    return votingDeviceService.deleteDevice(
        deviceId, extractIdFromAuth.getIdFromAuth(authentication));
  }

  @PreAuthorize("hasAuthority('USER')")
  @PostMapping
  public VotingDeviceResponse createDevice(
      @RequestBody VotingDevice votingDevice, Authentication authentication) {
    return votingDeviceService.addDeviceToUser(
        extractIdFromAuth.getIdFromAuth(authentication), votingDevice);
  }

  @PreAuthorize("hasAuthority('USER')")
  @GetMapping
  public List<VotingDeviceResponse> getAllUsersDevices(Authentication authentication) {
    return votingDeviceService.getAllDevicesToOwner(
        extractIdFromAuth.getIdFromAuth(authentication));
  }
}
