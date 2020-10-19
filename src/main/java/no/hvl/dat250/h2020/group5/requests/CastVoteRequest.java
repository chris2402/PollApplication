package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class CastVoteRequest {
  @NotBlank String vote;
}
