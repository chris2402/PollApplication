package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Poll;
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
    public List<Poll> getAllPublicPolls(){ return pollService.getAllPublicPolls(); }

    @PostMapping(path="/{userId}")
    public Poll createPoll(@RequestBody Poll body, @PathVariable Long userId){
        return pollService.createPoll(body, userId);
    }

    @DeleteMapping(path="/{pollId}")
    public boolean deletePoll(@PathVariable Long pollId){
        return pollService.deletePoll(pollId);
    }

    @RequestMapping
    public List<Poll> getOwnPolls(@RequestParam Long ownerId){
        return pollService.getOwnPolls(ownerId);
    }

    @GetMapping(path="/{pollId}")
    public Poll getPoll(@PathVariable long pollId){
        return pollService.getPoll(pollId);
    }

    @PatchMapping(path="/{pollId}")
    public boolean activatePoll(@PathVariable Long pollId){
        return pollService.activatePoll(pollId);
    }

    @GetMapping(path="/{pollId}/active")
    public boolean isPollActive(@PathVariable Long pollId){
        return pollService.getPollStatus(pollId);
    }

    @GetMapping(path="/{pollId}/votes")
    public int getNumberOfVotes(@PathVariable long pollId, @RequestParam String answerType) {
        return pollService.getNumberOfVotes(pollId, answerType);
    }

}
