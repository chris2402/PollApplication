package no.hvl.dat250.h2020.group5.repositories;

import no.hvl.dat250.h2020.group5.entities.VotingDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<VotingDevice, UUID> {
  Optional<VotingDevice> findVotingDeviceByDisplayName(String displayName);
}
