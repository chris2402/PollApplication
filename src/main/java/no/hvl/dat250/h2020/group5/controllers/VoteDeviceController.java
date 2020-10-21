package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votingDevice")
public class VoteDeviceController {

  final VoteService voteService;

  public VoteDeviceController(VoteService voteService) {
    this.voteService = voteService;
  }

  @PostMapping("/{pollId}")
  public List<Vote> vote(
      @PathVariable Long pollId, @RequestBody VoteRequestFromDevice voteRequestFromDevice) {
    return voteService.saveVotesFromDevice(pollId, voteRequestFromDevice);
  }
}
