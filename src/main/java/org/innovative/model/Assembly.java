package org.innovative.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Data
@Entity
@Table(name = "assembly")
public class Assembly {
    @Getter
    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    @Getter
    @Column(name = "qb_list_id", unique = true)
    private String qbListId;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    @Column(columnDefinition = "TEXT")
    private String description; // Maps to "Description" (Sales)

    @Column(columnDefinition = "TEXT")
    private String purchaseDescription; // Maps to "Purchase Description"

    @Setter
    @Getter
    private BigDecimal cost; // Maps to "Cost"

    @Setter
    @Getter
    private BigDecimal defaultPrice; // Maps to "Price"

    @Getter
    @Setter
    private String mpn; // Maps to "MPN"

    private String itemType; // Maps to "Type"

    private String preferredVendor; // Maps to "Preferred Vendor"

    private Double quantityOnHand; // Maps to "Quantity On Hand"

    private String assetAccount; // Maps to "Asset Account"

    private boolean active; // Maps to "Status"

    @OneToMany(mappedBy = "assembly", cascade = CascadeType.ALL)
    private List<AssemblyComponent> components;




    @Setter
    @Getter
    @Column(name = "\"type\"")
    private String type;



}