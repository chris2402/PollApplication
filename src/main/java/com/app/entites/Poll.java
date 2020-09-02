package com.app.entites;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question;
    private String alternativeA;
    private String alternativeB;

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn()
    private User user;

    @OneToMany
    private List<PollVote> pollVotes;

}
