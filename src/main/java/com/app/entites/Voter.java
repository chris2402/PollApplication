package com.app.entites;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Voter {

    @Id
    private Long id;

    @OneToMany(mappedBy = "voter")
    private List<Vote> votes;

    @OneToOne(mappedBy = "voter")
    private User user;

    public boolean isGuest() {
        return user == null;
    }
}
