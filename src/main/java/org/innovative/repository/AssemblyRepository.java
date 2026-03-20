package org.innovative.repository;

import org.innovative.model.Assembly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssemblyRepository extends JpaRepository<Assembly, UUID> {
    Assembly findByQbListId(String qbListId);
    List<Assembly> findByNameContainingIgnoreCase(String name);
}