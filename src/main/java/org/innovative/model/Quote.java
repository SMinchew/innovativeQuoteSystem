package org.innovative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quote")
@Data
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // MATCHES YOUR SCREENSHOT: qb_estimate_id
    @Column(name = "qb_estimate_id")
    private String qbEstimateId;

    @Enumerated(EnumType.STRING)
    private QuoteStatus status = QuoteStatus.DRAFT;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("quote")
    private List<QuoteLine> lines = new ArrayList<>();
}