package no.hvl.dat250.h2020.group5.eventCreators;

import no.hvl.dat250.h2020.group5.events.VoteReceivedEvent;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VoteEventCreator {

    @Autowired
    PollService pollService;


    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public void createAndPublishVoteReceivedEvent(Long pollId){
        UUID deviceId = UUID.fromString("6e7c8ece-e7ac-4295-bbf8-50d36f2d83cc");
        VotesResponse votesResponse = pollService.getNumberOfVotes(pollId, deviceId);
        VoteReceivedEvent voteReceivedEvent = new VoteReceivedEvent(this, votesResponse, pollId);
        applicationEventPublisher.publishEvent(voteReceivedEvent);
    }
}
