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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Poll> userPolls;

    @OneToOne(cascade = CascadeType.ALL)
    private Voter voter;




}
