package com.component.testing.demo.entity;

import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;
    private String description;

    @ManyToOne
    @JoinTable(
        name = "ticket_release",
        joinColumns = @JoinColumn(name = "ticket_fk"),
        inverseJoinColumns = @JoinColumn(name = "softwareRelease_fk")
    )
    private SoftwareRelease softwareRelease;

    private Status status;

    public Ticket() {}

    public Ticket(
        Integer id,
        String title,
        String description,
        SoftwareRelease softwareRelease,
        String status
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.softwareRelease = softwareRelease;
        this.status = Status.valueOf(status);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SoftwareRelease getRelease() {
        return softwareRelease;
    }

    public void setRelease(SoftwareRelease softwareRelease) {
        this.softwareRelease = softwareRelease;
    }

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED
    }
}
