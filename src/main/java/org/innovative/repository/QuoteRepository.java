package org.innovative.repository;

import org.innovative.model.Quote;
import org.innovative.model.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    Quote findFirstByStatus(QuoteStatus status);
}