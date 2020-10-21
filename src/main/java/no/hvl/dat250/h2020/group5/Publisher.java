package no.hvl.dat250.h2020.group5;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

    private final RabbitTemplate rabbitTemplate;

    public Publisher( RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String info) {
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(Main.exchangeName, info);
    }

}
