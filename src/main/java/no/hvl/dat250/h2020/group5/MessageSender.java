package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private DeviceRepository deviceRepository;

    public void sendToSubscriber(String destination, String payload) {
        try {
            simpMessagingTemplate.convertAndSend(destination, payload);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }
}
