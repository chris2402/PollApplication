package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.Poll;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class Publisher {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${pollapplication.rabbitmq.exchange}")
    private String exchange;

    @Value("${pollapplication.rabbitmq.routingkey}")
    private String routingkey;

    public void send(String poll) {
        rabbitTemplate.convertAndSend(exchange, routingkey, poll);
        System.out.println("Send msg = " + poll);

    }
}
