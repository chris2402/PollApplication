package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import no.hvl.dat250.h2020.group5.repositories.DeviceRepository;
import no.hvl.dat250.h2020.group5.responses.VotingDeviceResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class VotingDeviceService {

  final PasswordEncoder encoder;
  private final DeviceRepository deviceRepository;

  public VotingDeviceService(DeviceRepository deviceRepository, PasswordEncoder encoder) {
    this.deviceRepository = deviceRepository;
    this.encoder = encoder;
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

  public VotingDeviceResponse createDevice() {
    Integer name = new Random().nextInt(10000000);
    VotingDevice device =
        new VotingDevice().password(encoder.encode(name.toString())).username(name.toString());
    return new VotingDeviceResponse(deviceRepository.save(device));
  }
}
