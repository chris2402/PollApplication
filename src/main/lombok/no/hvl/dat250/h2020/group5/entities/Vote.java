package no.hvl.dat250.h2020.group5.entities;

import no.hvl.dat250.h2020.group5.enums.AnswerType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@IdClass(VoteId.class)
public class Vote {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="voter_id")
    private Voter voter;

    @Id
    @JoinColumn(name="poll_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Poll poll;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;

}
