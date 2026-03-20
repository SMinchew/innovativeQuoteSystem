package org.innovative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "quote_line")
@Data
public class QuoteLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    @JsonIgnoreProperties("lines") // This stops the loop!
    private Quote quote;

    @ManyToOne
    @JoinColumn(name = "assembly_id") // Fixes the red error on your line 23
    private Assembly assembly;

    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}