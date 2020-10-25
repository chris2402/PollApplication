package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.enums.PollVisibilityType;
import no.hvl.dat250.h2020.group5.service.PollService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PublisherTest {

    @Mock RabbitTemplate rabbitTemplate;

    @Mock
    PollService pollService;

    @InjectMocks @Spy
    Publisher publisher;

    @Test
    public void shouldSendMessageWhenFinishedPoll() {
        when(pollService.getAllFinishedPublicPolls()).thenReturn(Arrays.asList(new Poll().visibilityType(PollVisibilityType.PUBLIC).question("Do you like pizza?")));
        Thread publisherThread = new Thread(publisher);
        publisherThread.start();
        try {
            Thread.sleep(5000);
            publisher.stop();
            verify(publisher, times(1)).send(anyString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
