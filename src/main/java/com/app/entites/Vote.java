package com.app.entites;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean isGuest;
    private Character vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn()
    private PollVote pollVote;

}
