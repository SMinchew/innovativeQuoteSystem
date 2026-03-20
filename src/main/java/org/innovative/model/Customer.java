package org.innovative.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    private String qbListId;

    @Column(nullable = false)
    private String name;

    private String email;
    private String phone;
    private String address;

    // Getters & Setters
    public UUID getId() { return id; }
    public String getQbListId() { return qbListId; }
    public void setQbListId(String qbListId) { this.qbListId = qbListId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}