package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.VoteRequestFromDevice;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/votingDevice")
public class VoteDeviceController {

  final VoteService voteService;

  public VoteDeviceController(VoteService voteService) {
    this.voteService = voteService;
  }

  @PostMapping
  public @ResponseBody List<Vote> vote(@RequestBody VoteRequestFromDevice voteRequestFromDevice) {
    return voteService.saveVotesFromDevice(voteRequestFromDevice);
  }
}
