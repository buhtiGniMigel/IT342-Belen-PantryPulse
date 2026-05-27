package edu.cit.belen.pantrypulse.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {
}
