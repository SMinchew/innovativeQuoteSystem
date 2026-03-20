package org.innovative.controller;

import org.innovative.model.*;
import org.innovative.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private AssemblyRepository assemblyRepository;

    @Autowired
    private QuoteLineRepository quoteLineRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public ResponseEntity<List<QuoteSummary>> getAll() {
        List<Quote> quotes = quoteRepository.findAll();
        List<QuoteSummary> summaries = quotes.stream().map(quote -> {
            String name = (quote.getCustomer() != null) ? quote.getCustomer().getName() : "Unknown";
            int lines = (quote.getLines() != null) ? quote.getLines().size() : 0;

            // 1. UUID, 2. String, 3. QuoteStatus, 4. LocalDateTime, 5. int
            return new QuoteSummary(
                    quote.getId(),
                    name,
                    quote.getStatus(),
                    quote.getCreatedAt(),
                    lines
            );
        }).toList();
        return ResponseEntity.ok(summaries);
    }

    @PostMapping
    public Quote create(@RequestBody Quote quote) {
        quote.setStatus(QuoteStatus.DRAFT);
        quote.setCreatedAt(LocalDateTime.now());
        return quoteRepository.save(quote);
    }

    @PostMapping("/{id}/lines")
    public ResponseEntity<Quote> addLine(@PathVariable UUID id, @RequestBody QuoteLine line) {
        return quoteRepository.findById(id).map(quote -> {
            // 1. Find the Trailer Model (Assembly)
            Assembly assembly = assemblyRepository.findById(line.getAssembly().getId())
                    .orElseThrow(() -> new RuntimeException("Model not found"));

            // 2. Map the relationship properly
            line.setQuote(quote);
            line.setAssembly(assembly);
            line.setUnitPrice(assembly.getDefaultPrice());

            // 3. Simple math (Ensure lineTotal field exists in your QuoteLine model)
            if (assembly.getDefaultPrice() != null) {
                line.setLineTotal(assembly.getDefaultPrice().multiply(new BigDecimal(line.getQuantity())));
            }

            // 4. Save the line first
            quoteLineRepository.save(line);

            // 5. Return the updated quote
            return ResponseEntity.ok(quoteRepository.findById(id).get());
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getById(@PathVariable UUID id) {
        return quoteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/total")
    public ResponseEntity<BigDecimal> getTotal(@PathVariable UUID id) {
        return quoteRepository.findById(id).map(quote -> {
            BigDecimal total = quote.getLines().stream()
                    .map(line -> line.getLineTotal() != null ? line.getLineTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return ResponseEntity.ok(total);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!quoteRepository.existsById(id)) return ResponseEntity.notFound().build();
        quoteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Quote> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        return quoteRepository.findById(id).map(quote -> {
            quote.setStatus(QuoteStatus.valueOf(status));
            return ResponseEntity.ok(quoteRepository.save(quote));
        }).orElse(ResponseEntity.notFound().build());
    }
}