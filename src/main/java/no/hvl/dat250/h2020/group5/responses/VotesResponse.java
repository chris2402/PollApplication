package no.hvl.dat250.h2020.group5.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotesResponse {
  Integer yes = 0;
  Integer no = 0;

  public VotesResponse yes(int numberOfYes) {
    this.setYes(numberOfYes);
    return this;
  }

  public VotesResponse no(int numberOfNo) {
    this.setNo(numberOfNo);
    return this;
  }

  public String toJSON() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
