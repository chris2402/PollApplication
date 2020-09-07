package no.hvl.dat250.h2020.group5.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity // (name = "VotingDevice")
public class VotingDevice extends Device {
}
