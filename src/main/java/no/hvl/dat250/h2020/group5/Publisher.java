package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.responses.PollResponse;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Publisher implements Runnable{

    private final RabbitTemplate rabbitTemplate;
    private final PollService pollService;

    public Publisher(RabbitTemplate rabbitTemplate, PollService pollService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pollService = pollService;
    }

    public void send(String message) {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Main.topicExchangeName, Main.routingKey, message);
    }

    /**
     * Ask {@link PollService} for finished and public polls each 5 seconds and publish them.
     */
    @Override
    public void run() {
        while (true) {
            List<Poll> finishedPolls = pollService.getAllFinishedPublicPolls();
            if (!finishedPolls.isEmpty()) {
                for (Poll poll : finishedPolls) {
                    send(poll.toString());
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
