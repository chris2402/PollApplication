package no.hvl.dat250.h2020.group5.controllers;

import no.hvl.dat250.h2020.group5.entities.Vote;
import no.hvl.dat250.h2020.group5.requests.CastVoteRequest;
import no.hvl.dat250.h2020.group5.service.VoteService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/votes")
public class VoteController {

    final
    VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping()
    public Boolean castVote(@Valid @RequestBody CastVoteRequest castVoteRequest){
        return voteService.vote(castVoteRequest);
    }

    @PatchMapping()
    public Boolean changeVote(@Valid @RequestBody CastVoteRequest castVoteRequest){
        return voteService.changeVote(castVoteRequest);
    }

    @GetMapping()
    public Vote findVote(@RequestParam Long userId, @RequestParam String pollId){
        return voteService.findVote(pollId, userId);
    }
}
