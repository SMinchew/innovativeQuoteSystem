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
    private String description;

    @Column(columnDefinition = "TEXT")
    private String purchaseDescription;

    @Setter
    @Getter
    private BigDecimal cost;

    @Setter
    @Getter
    private BigDecimal defaultPrice;

    @Getter
    @Setter
    private String mpn;

    private String itemType;

    private String preferredVendor;

    private Double quantityOnHand;

    private String assetAccount;

    private boolean active;

    @OneToMany(mappedBy = "assembly", cascade = CascadeType.ALL)
    private List<AssemblyComponent> components;




    @Setter
    @Getter
    @Column(name = "\"type\"")
    private String type;



}