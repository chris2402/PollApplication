package no.hvl.dat250.h2020.group5.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private User deviceUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getDeviceUser() {
        return deviceUser;
    }

    public void setDeviceUser(User deviceUser) {
        this.deviceUser = deviceUser;
    }
}
