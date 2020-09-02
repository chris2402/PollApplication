package com.app.entites;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    private Long id;

    private String username;
    private String password;

    @OneToMany
    @PrimaryKeyJoinColumn()
    private List<Poll> userPolls;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name ="vote_pk")
    private List<Vote> votes;




}
