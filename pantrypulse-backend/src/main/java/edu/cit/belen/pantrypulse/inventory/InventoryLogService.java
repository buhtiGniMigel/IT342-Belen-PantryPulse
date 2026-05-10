package edu.cit.belen.pantrypulse.inventory;

import java.util.List;
import org.springframework.stereotype.Service;
import edu.cit.belen.pantrypulse.inventory.InventoryLogService;

@Service
public class InventoryLogService {
    private final InventoryLogRepository repo;
    public InventoryLogService(InventoryLogRepository repo) { this.repo = repo; }
    public List<InventoryLog> getAllInventory() { return repo.findAll(); }
    public InventoryLog addInventory(InventoryLog item) { return repo.save(item); }
    public void deleteInventory(Long id) { repo.deleteById(id); }
}