package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.User;
import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.exceptions.NotFoundException;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.repositories.UserRepository;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VotingDeviceService {

  private final DeviceRepository deviceRepository;
  private final UserRepository userRepository;

  public VotingDeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
    this.deviceRepository = deviceRepository;
    this.userRepository = userRepository;
  }

  public List<VotingDeviceResponse> getAllDevices() {
    List<VotingDeviceResponse> votingDevices = new ArrayList<>();
    deviceRepository
        .findAll()
        .forEach(device -> votingDevices.add(new VotingDeviceResponse(device)));
    return votingDevices;
  }

  public VotingDeviceResponse findDevice(String name) {
    Optional<VotingDevice> foundDevice = deviceRepository.findVotingDeviceByUsername(name);
    if (foundDevice.isEmpty()) {
      return null;
    }
    return new VotingDeviceResponse(foundDevice.get());
  }

  public VotingDeviceResponse addDeviceToUser(Long id, VotingDevice votingDevice) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new NotFoundException("Cannot add device to empty user");
    }
    votingDevice.username(UUID.randomUUID().toString());
    user.get().getVotingDevices().add(votingDevice);
    return new VotingDeviceResponse(
        userRepository.save(user.get()).getVotingDevices().stream()
            .filter(device -> device.getUsername().equals(votingDevice.getUsername()))
            .collect(Collectors.toList())
            .get(0));
  }

  public List<VotingDeviceResponse> getAllDevicesToOwner(Long id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return user.get().getVotingDevices().stream()
        .map(VotingDeviceResponse::new)
        .collect(Collectors.toList());
  }

  public boolean deleteDevice(Long deviceId, Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    Optional<VotingDevice> votingDevice = deviceRepository.findById(deviceId);
    if (votingDevice.isEmpty()) {
      throw new NotFoundException("Device not found");
    }

    boolean deleted = user.get().getVotingDevices().remove(votingDevice.get());
    userRepository.save(user.get());
    return deleted;
  }
}
