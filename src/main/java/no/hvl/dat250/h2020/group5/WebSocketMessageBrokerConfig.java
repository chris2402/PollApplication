package no.hvl.dat250.h2020.group5;

import no.hvl.dat250.h2020.group5.entities.Poll;
import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.exceptions.NotAllowedDevice;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.repositories.PollRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Optional;
import java.util.UUID;

@Component
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    final
    DeviceRepository deviceRepository;

    final
    PollRepository pollRepository;

    final
    UserRepository userRepository;

    public WebSocketMessageBrokerConfig(DeviceRepository deviceRepository, PollRepository pollRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String deviceId = accessor.getFirstNativeHeader("deviceId");
                    String pollId = accessor.getFirstNativeHeader("pollId");
                    checkIfConnectionShouldBeAllowed(UUID.fromString(deviceId), Long.parseLong(pollId));
                }
                return message;
            }
        });
    }

    private void checkIfConnectionShouldBeAllowed(UUID deviceId, Long pollId){
        Optional<Poll> optionalPoll = pollRepository.findById(pollId);
        if(optionalPoll.isEmpty()){
            throw new NotFoundException("Poll not found");
        }
        User user = optionalPoll.get().getPollOwner();
        boolean allowedDevice = user.getVotingDevices().stream().
                anyMatch(device -> device.getId().equals(deviceId));
        if(!allowedDevice){
            throw new NotAllowedDevice("Device does not belong to poll owner");
        }
    }



}
