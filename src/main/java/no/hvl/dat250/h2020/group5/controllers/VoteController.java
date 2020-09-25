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
    public Vote castVote(@Valid @RequestBody CastVoteRequest castVoteRequest){
        return voteService.vote(castVoteRequest);
    }

    @PatchMapping("/{id}")
    public Boolean changeVote(@PathVariable Long id, @RequestParam String newAnswer){
        return voteService.changeVote(id, newAnswer);
    }

    @GetMapping()
    public Vote findVote(@RequestParam Long userId, @RequestParam Long pollId){
        return voteService.findVote(pollId, userId);
    }

    @DeleteMapping("/{id}")
    public boolean deleteVote(@PathVariable Long id){
        return voteService.deleteVote(id);
    }
}
