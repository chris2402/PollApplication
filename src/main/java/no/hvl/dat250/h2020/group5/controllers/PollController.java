package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
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
    public List<Poll> getAllPublicPolls() {
        return pollService.getAllPublicPolls();
    }

    @PostMapping(path = "/{userId}")
    public Poll createPoll(@RequestBody Poll body, @PathVariable Long userId) {
        return pollService.createPoll(body, userId);
    }

    @DeleteMapping(path = "/{pollId}/{userId}")
    public boolean deletePoll(@PathVariable Long pollId, @PathVariable Long userId) {
        return pollService.deletePoll(pollId, userId);
    }

    @GetMapping(path = "/{pollId}")
    public Poll getPoll(@PathVariable Long pollId) {
        return pollService.getPoll(pollId);
    }

    @PatchMapping(path = "/{pollId}")
    public boolean activatePoll(@PathVariable Long pollId) {
        return pollService.activatePoll(pollId);
    }

    @GetMapping(path = "/{pollId}/active")
    public boolean isPollActive(@PathVariable Long pollId) {
        return pollService.getPollStatus(pollId);
    }

    @GetMapping(path = "/{pollId}/votes")
    public VotesResponse getNumberOfVotes(@PathVariable Long pollId) {
        return pollService.getNumberOfVotes(pollId);
    }
}
