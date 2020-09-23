package no.hvl.dat250.h2020.group5.controllers;

import com.fasterxml.jackson.core.JsonParser;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import no.hvl.dat250.h2020.group5.entities.Guest;
import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.service.GuestService;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @RequestMapping(path="/{poll-id}")
    public Poll getPoll(@PathVariable("poll-id") long pollId){
        return pollService.getPoll(pollId);
    }

    @PatchMapping(path="/{poll-id}")
    public boolean changePollStatus(@PathVariable("poll-id") String pollId,
            @RequestBody boolean status,
            @RequestBody String userId){
        return pollService.changePollStatus(pollId, userId, status);
    }

}
