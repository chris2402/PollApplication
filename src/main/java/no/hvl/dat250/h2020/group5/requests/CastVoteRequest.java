package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class CastVoteRequest {
    String pollId;
    Long userId;
    String Vote;
}
