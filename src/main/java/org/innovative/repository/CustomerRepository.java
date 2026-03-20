package org.innovative.repository;

import org.innovative.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Customer findByQbListId(String qbListId);
    List<Customer> findByNameContainingIgnoreCase(String name);
}