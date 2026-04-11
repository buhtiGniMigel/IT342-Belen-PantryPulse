package edu.cit.belen.pantrypulse.repository;

import edu.cit.belen.pantrypulse.model.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
}