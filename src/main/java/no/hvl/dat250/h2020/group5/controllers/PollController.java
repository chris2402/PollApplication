package no.hvl.dat250.h2020.group5.controllers;


import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO: Better feedback
//TODO: userId from JWT or similar
@RestController
@RequestMapping("/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping
    public List<Poll> getAllPublicPolls(){ return pollService.getAllPublicPolls(); }

    @RequestMapping(method = RequestMethod.POST)
    //TODO: Remove userId and use JWT or similar.
    public Poll createPoll(@RequestBody Poll body){
        return pollService.createPoll(body);
    }

    @RequestMapping(method = RequestMethod.POST, path="/{user-id}")
    //TODO: Remove userId and use JWT or similar.
    public Poll createPoll2(@RequestBody Poll body, @PathVariable("user-id") Long userId){
        return pollService.createPoll2(body, userId);
    }

    @DeleteMapping(path="/{poll-id}")
    public boolean deletePoll(@PathVariable("poll-id") Long pollId){
        return pollService.deletePoll(pollId);
    }

    @RequestMapping
    public List<Poll> getOwnPolls(@RequestParam("owner-id") Long ownerId){
        return pollService.getOwnPolls(ownerId);
    }

    @GetMapping(path="/{poll-id}")
    public Poll getPoll(@PathVariable("poll-id") long pollId){
        return pollService.getPoll(pollId);
    }

    @PatchMapping(path="/{poll-id}")
    public boolean changePollStatus(@PathVariable("poll-id") String pollId){
        return pollService.changePollStatus(pollId);
    }

    @GetMapping(path="/{poll-id}/votes")
    public int getNumberOfVotes(@PathVariable("poll-id") long pollId,
                   @RequestParam String answerType) {
        return pollService.getNumberOfVotes(pollId, answerType);
    }

}
