package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity // (name = "User")
public class User extends Voter {

    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pollOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Poll> userPolls = new ArrayList<>();

}