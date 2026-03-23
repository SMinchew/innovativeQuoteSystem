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

    @Autowired
    private org.innovative.pdf.QuotePdfService quotePdfService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> downloadPdf(@PathVariable UUID id) {
        return quoteRepository.findById(id).map(quote -> {
            try {
                byte[] pdf = quotePdfService.generateQuotePdf(quote);
                return ResponseEntity.ok()
                        .header("Content-Type", "application/pdf")
                        .header("Content-Disposition", "attachment; filename=quote-" + id + ".pdf")
                        .body(pdf);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }).orElse(ResponseEntity.notFound().build());
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

            Assembly assembly = assemblyRepository.findById(line.getAssembly().getId())
                    .orElseThrow(() -> new RuntimeException("Model not found"));


            line.setQuote(quote);
            line.setAssembly(assembly);
            line.setUnitPrice(assembly.getDefaultPrice());


            if (assembly.getDefaultPrice() != null) {
                line.setLineTotal(assembly.getDefaultPrice().multiply(new BigDecimal(line.getQuantity())));
            }


            quoteLineRepository.save(line);


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