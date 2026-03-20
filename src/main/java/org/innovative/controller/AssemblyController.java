package org.innovative.controller;

import org.innovative.model.Assembly;
import org.innovative.repository.AssemblyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assemblies")
public class AssemblyController {

    @Autowired
    private AssemblyRepository assemblyRepository;

    // Get all assemblies
    @GetMapping
    public List<Assembly> getAll() {
        return assemblyRepository.findAll();
    }

    // Get single assembly by ID
    @GetMapping("/{id}")
    public ResponseEntity<Assembly> getById(@PathVariable UUID id) {
        return assemblyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search assemblies by name
    @GetMapping("/search")
    public List<Assembly> search(@RequestParam String name) {
        return assemblyRepository.findByNameContainingIgnoreCase(name);
    }

    // Create assembly manually (for testing before QB sync)
    @PostMapping
    public Assembly create(@RequestBody Assembly assembly) {
        return assemblyRepository.save(assembly);
    }

    // Update assembly
    @PutMapping("/{id}")
    public ResponseEntity<Assembly> update(@PathVariable UUID id,
                                           @RequestBody Assembly updated) {
        return assemblyRepository.findById(id).map(assembly -> {
            assembly.setName(updated.getName());
            assembly.setDescription(updated.getDescription());
            assembly.setCost(updated.getCost());
            assembly.setDefaultPrice(updated.getDefaultPrice());
            return ResponseEntity.ok(assemblyRepository.save(assembly));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete assembly
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return assemblyRepository.findById(id).map(assembly -> {
            assemblyRepository.delete(assembly);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}