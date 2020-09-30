package no.hvl.dat250.h2020.group5.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Poll;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PollResponse {

    private Long id;
    private String pollName;
    private String question;
    private Date startTime;
    private Integer pollDuration;

    public PollResponse(Poll poll) {
        this.id = poll.getId();
        this.pollDuration = poll.getPollDuration();
        this.pollName = poll.getName();
        this.question = poll.getQuestion();
        this.startTime = poll.getStartTime();
    }
}
