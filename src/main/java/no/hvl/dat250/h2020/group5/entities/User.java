package no.hvl.dat250.h2020.group5.entities;

import javax.persistence.Entity;

@Entity(name = "User")
public class User extends Voter {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}