package org.innovative.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class QuoteSummary {
    private UUID id;
    private String customerName;
    private QuoteStatus status;
    private LocalDateTime createdAt;
    private int lineCount;
    private BigDecimal total;


    public QuoteSummary(UUID id, String customerName, QuoteStatus status, LocalDateTime createdAt, int lineCount, BigDecimal total) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = createdAt;
        this.lineCount = lineCount;
        this.total = total;
    }


    public UUID getId() { return id; }
    public String getCustomerName() { return customerName; }
    public QuoteStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getLineCount() { return lineCount; }

    public BigDecimal getTotal() { return total; }
}