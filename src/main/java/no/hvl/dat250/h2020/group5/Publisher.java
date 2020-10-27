package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.responses.VotesResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class Publisher implements Runnable {

  private final RabbitTemplate rabbitTemplate;
  private final PollService pollService;
  private final WebClient webClient =
      WebClient.create("https://dweet.io/dweet/for/poll-application-group5");
  private boolean running = true;

  private ArrayList<Poll> sentPolls;

  public Publisher(RabbitTemplate rabbitTemplate, PollService pollService) {
    this.rabbitTemplate = rabbitTemplate;
    this.pollService = pollService;
    this.sentPolls = new ArrayList<>();
    Logger.getLogger("Publisher").info("Initialized publisher");
  }

  public void send(String message) {
    System.out.println("Sending message...");
    rabbitTemplate.convertAndSend(Main.topicExchangeName, Main.routingKey, message);
  }

  public void sendDweet(String name, String question, String status, VotesResponse votes) {
    Logger.getLogger("Publisher").info("Sending dweet when poll is finished...");
    Mono<String> response =
        webClient
            .get()
            .uri(
                "?pollName="
                    + name
                    + "&question="
                    + question
                    + "&status="
                    + status
                    + "&yes="
                    + votes.getYes()
                    + "&no="
                    + votes.getNo())
            .retrieve()
            .bodyToMono(String.class);
    Logger.getLogger("Publisher").info(response.block());
  }

  /** Ask {@link PollService} for finished and public polls each 5 seconds and publish them. */
  @Override
  public void run() {
    Logger.getLogger("Publisher").info("Started publisher");
    while (running) {
      List<Poll> finishedPolls = pollService.getAllFinishedPublicPolls();
      if (!finishedPolls.isEmpty()) {
        for (Poll poll : finishedPolls) {
          if (!sentPolls.contains(poll)) {
            VotesResponse votes = pollService.getNumberOfVotes(poll.getId());
            sendDweet(poll.getName(), poll.getQuestion(), "completed", votes);
            send(getPollJSON(poll, votes));
            sentPolls.add(poll);
          }
        }
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    this.running = false;
  }

  private String getPollJSON(Poll poll, VotesResponse votes) {
    String json =
        "{ \"id\":"
            + poll.getId()
            + ", "
            + "\"question\":\""
            + poll.getQuestion()
            + "\""
            + ", "
            + "\"votes\":"
            + votes.toJSON()
            + "}";
    return json;
  }
}
