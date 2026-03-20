package org.innovative.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class AssemblyComponent {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assembly_id")
    private Assembly assembly;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private BigDecimal quantity;

    // Getters & Setters
    public UUID getId() { return id; }
    public Assembly getAssembly() { return assembly; }
    public void setAssembly(Assembly assembly) { this.assembly = assembly; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}