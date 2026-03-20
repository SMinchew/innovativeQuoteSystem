package org.innovative.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Item {
    @Id
    @GeneratedValue
    private UUID id;
    private String qbListId;
    private String name;
    private String description;
    private BigDecimal cost;
    private BigDecimal price;

    // Getters & Setters
    public UUID getId() { return id; }
    public String getQbListId() { return qbListId; }
    public void setQbListId(String qbListId) { this.qbListId = qbListId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}