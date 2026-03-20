package org.innovative.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuoteSummary {
    private UUID id;
    private String customerName;
    private QuoteStatus status;
    private LocalDateTime createdAt;
    private int lineCount; // Add this field

    // Update constructor to accept 5 arguments
    public QuoteSummary(UUID id, String customerName, QuoteStatus status, LocalDateTime createdAt, int lineCount) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = createdAt;
        this.lineCount = lineCount;
    }

    // Getters
    public UUID getId() { return id; }
    public String getCustomerName() { return customerName; }
    public QuoteStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getLineCount() { return lineCount; }
}