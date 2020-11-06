package no.hvl.dat250.h2020.group5.requests;

import lombok.Getter;
import lombok.Setter;
import no.hvl.dat250.h2020.group5.entities.Poll;

import java.util.List;

@Getter
@Setter
public class CreateOrUpdatePollRequest {

  Poll poll;
  List<String> emails;

  public CreateOrUpdatePollRequest poll(Poll poll) {
    setPoll(poll);
    return this;
  }

  public CreateOrUpdatePollRequest emails(List<String> emails) {
    setEmails(emails);
    return this;
  }
}
