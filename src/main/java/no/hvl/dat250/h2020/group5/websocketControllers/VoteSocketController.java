package no.hvl.dat250.h2020.group5.websocketControllers;

import no.hvl.dat250.h2020.group5.MessageSender;
import no.hvl.dat250.h2020.group5.events.VoteReceivedEvent;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class VoteSocketController {

    @Autowired
    private PollService pollService;

    @Autowired
    private MessageSender messageSender;

    @MessageMapping("/poll/{pollId}/votes")
    public void getVotes(@DestinationVariable Long pollId, @Header("deviceId") String deviceId){
        VotesResponse votesResponse = pollService.getNumberOfVotes(pollId, UUID.fromString(deviceId));
        sendVotesToSubscriber(pollId, votesResponse);

    }

    @EventListener
    public void onVoteAdded(VoteReceivedEvent voteReceivedEvent){
        sendVotesToSubscriber(voteReceivedEvent.getPollId(), voteReceivedEvent.getVotes());
    }

    private void sendVotesToSubscriber(Long pollId,  VotesResponse votesResponse){
        messageSender.sendToSubscriber("/topic/poll/" + pollId + "/votes", votesResponse.toJSON());
    }
}
