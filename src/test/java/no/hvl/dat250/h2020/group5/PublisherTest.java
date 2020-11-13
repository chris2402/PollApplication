package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PublisherTest {

  @Mock RabbitTemplate rabbitTemplate;

  @Mock PollService pollService;

  @InjectMocks @Spy Publisher publisher;

  private Poll pizzaPoll;
  private Poll fruitPoll;

  @BeforeEach
  public void setUp() {
    this.pizzaPoll =
        new Poll().visibilityType(PollVisibilityType.PUBLIC).question("Do you like pizza?");
    this.fruitPoll =
        new Poll().visibilityType(PollVisibilityType.PUBLIC).question("Do you like fruit?");
    pizzaPoll.setId(1L);
    fruitPoll.setId(2L);

    when(pollService.getNumberOfVotesAsAdmin(pizzaPoll.getId())).thenReturn(new VotesResponse());
    when(pollService.getNumberOfVotesAsAdmin(fruitPoll.getId())).thenReturn(new VotesResponse());
  }

  @Test
  public void shouldSendMessageWhenFinishedPoll() {
    when(pollService.getAllFinishedPublicPolls()).thenReturn(Arrays.asList(pizzaPoll));
    Thread publisherThread = new Thread(publisher);
    publisherThread.start();
    try {
      Thread.sleep(5000);
      publisher.stop();
      verify(publisher, times(2)).send(anyString());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldSendNewMessageAfterFiveSeconds() {
    when(pollService.getAllFinishedPublicPolls()).thenReturn(Arrays.asList(pizzaPoll));
    Thread publisherThread = new Thread(publisher);
    publisherThread.start();
    try {
      Thread.sleep(5000);
      when(pollService.getAllFinishedPublicPolls()).thenReturn(Arrays.asList(pizzaPoll, fruitPoll));
      Thread.sleep(6000);
      publisher.stop();
      verify(publisher, times(1))
          .send("{ \"id\":1, \"question\":\"Do you like pizza?\", \"votes\":{\"yes\":0,\"no\":0}}");
      verify(publisher, times(1))
          .send("{ \"id\":2, \"question\":\"Do you like fruit?\", \"votes\":{\"yes\":0,\"no\":0}}");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void shouldSendVoteResponse() {
    when(pollService.getAllFinishedPublicPolls()).thenReturn(Arrays.asList(pizzaPoll));
    when(pollService.getNumberOfVotesAsAdmin(pizzaPoll.getId()))
        .thenReturn(new VotesResponse().no(1).yes(0));
    Thread publisherThread = new Thread(publisher);
    publisherThread.start();
    try {
      Thread.sleep(5000);
      publisher.stop();
      verify(publisher, times(1))
          .send("{ \"id\":1, \"question\":\"Do you like pizza?\", \"votes\":{\"yes\":0,\"no\":1}}");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
