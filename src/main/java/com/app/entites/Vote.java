package com.app.entites;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Vote {

    @Id
    private Long id;
    private int voteA;
    private int voteB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id")
    private Voter voter;

}
