package org.innovative.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assembly")
public class Assembly {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "qb_list_id", unique = true)
    private String qbListId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; // Maps to "Description" (Sales)

    @Column(columnDefinition = "TEXT")
    private String purchaseDescription; // Maps to "Purchase Description"

    private BigDecimal cost; // Maps to "Cost"

    private BigDecimal defaultPrice; // Maps to "Price"

    private String mpn; // Maps to "MPN" (Manufacturer Part Number)

    private String itemType; // Maps to "Type" (Assembly, Part, etc.)

    private String preferredVendor; // Maps to "Preferred Vendor"

    private Double quantityOnHand; // Maps to "Quantity On Hand"

    private String assetAccount; // Maps to "Asset Account"

    private boolean active; // Maps to "Status" (Active/Inactive)

    @OneToMany(mappedBy = "assembly", cascade = CascadeType.ALL)
    private List<AssemblyComponent> components;

    // Getters & Setters
    public UUID getId() { return id; }

    public String getQbListId() { return qbListId; }
    public void setQbListId(String qbListId) { this.qbListId = qbListId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPurchaseDescription() { return purchaseDescription; }
    public void setPurchaseDescription(String purchaseDescription) { this.purchaseDescription = purchaseDescription; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(BigDecimal defaultPrice) { this.defaultPrice = defaultPrice; }

    public String getMpn() { return mpn; }
    public void setMpn(String mpn) { this.mpn = mpn; }

    @Column(name = "\"type\"") // Double quotes tell Postgres this is a column name, not a keyword
    private String type;

    // Update your Getter and Setter
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPreferredVendor() { return preferredVendor; }
    public void setPreferredVendor(String preferredVendor) { this.preferredVendor = preferredVendor; }

    public Double getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Double quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public String getAssetAccount() { return assetAccount; }
    public void setAssetAccount(String assetAccount) { this.assetAccount = assetAccount; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<AssemblyComponent> getComponents() { return components; }
    public void setComponents(List<AssemblyComponent> components) { this.components = components; }
}