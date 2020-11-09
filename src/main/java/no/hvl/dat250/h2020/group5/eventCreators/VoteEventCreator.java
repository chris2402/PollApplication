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
        VotesResponse votesResponse = pollService.getNumberOfVotesSocket(pollId);
        VoteReceivedEvent voteReceivedEvent = new VoteReceivedEvent(this, votesResponse, pollId);
        applicationEventPublisher.publishEvent(voteReceivedEvent);
    }
}
