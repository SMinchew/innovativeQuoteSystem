package org.innovative.repository;

import org.innovative.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    // Spring Data JPA auto-generates all basic DB operations
    Item findByQbListId(String qbListId); // useful for upserts during sync
}