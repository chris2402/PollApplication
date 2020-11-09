package no.hvl.dat250.h2020.group5.events;

import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.UUID;


public class VoteReceivedEvent extends ApplicationEvent {

    private VotesResponse votes;

    private Long pollId;

    public VoteReceivedEvent(Object source, VotesResponse votes, Long pollId) {
        super(source);
        this.votes = votes;
        this.pollId = pollId;
    }

    public VotesResponse getVotes() {
        return votes;
    }

    public Long getPollId() {
        return pollId;
    }
}
