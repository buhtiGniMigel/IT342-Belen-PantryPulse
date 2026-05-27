package edu.cit.belen.pantrypulse.pantry;

import edu.cit.belen.pantrypulse.inventory.InventoryLog;
import edu.cit.belen.pantrypulse.inventory.InventoryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class PantryItemController {

    @Autowired
    private PantryItemRepository repository;

    @Autowired
    private InventoryLogRepository logRepository;

    @jakarta.annotation.PostConstruct
    public void initMockLogs() {
        if (logRepository.count() == 0) {
            InventoryLog log1 = new InventoryLog();
            log1.setItemName("Fresh Milk");
            log1.setCategory("Dairy");
            log1.setAction("Added: 2.0 unit(s)");
            log1.setUserId(1L);
            logRepository.save(log1);

            InventoryLog log2 = new InventoryLog();
            log2.setItemName("Chicken Breast");
            log2.setCategory("Meat");
            log2.setAction("Added: 1.5 unit(s)");
            log2.setUserId(1L);
            logRepository.save(log2);

            InventoryLog log3 = new InventoryLog();
            log3.setItemName("Organic Bananas");
            log3.setCategory("Produce");
            log3.setAction("Updated to 6.0 unit(s)");
            log3.setUserId(1L);
            logRepository.save(log3);

            InventoryLog log4 = new InventoryLog();
            log4.setItemName("Whole Wheat Bread");
            log4.setCategory("Bakery");
            log4.setAction("Removed/Consumed");
            log4.setUserId(1L);
            logRepository.save(log4);
        }
    }

    @GetMapping
    public List<PantryItem> getAll() {
        List<PantryItem> items = repository.findAll();
        items.forEach(PantryItem::calculateStatus);
        return items;
    }

    @PostMapping
    public ResponseEntity<PantryItem> create(@RequestBody PantryItem item) {
        item.calculateStatus();
        PantryItem saved = repository.save(item);
        
        // Write global inventory log!
        try {
            InventoryLog log = new InventoryLog();
            log.setItemName(saved.getItemName());
            log.setCategory(saved.getCategory());
            log.setAction("Added: " + saved.getQuantity() + " unit(s) in " + saved.getCategory());
            log.setUserId(1L);
            logRepository.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PantryItem> update(@PathVariable Long id, @RequestBody PantryItem updatedData) {
        return repository.findById(id)
                .map(item -> {
                    item.setItemName(updatedData.getItemName());
                    item.setCategory(updatedData.getCategory());
                    item.setQuantity(updatedData.getQuantity());
                    item.setExpiryDate(updatedData.getExpiryDate());
                    item.calculateStatus();
                    PantryItem saved = repository.save(item);
                    
                    // Write global inventory log!
                    try {
                        InventoryLog log = new InventoryLog();
                        log.setItemName(saved.getItemName());
                        log.setCategory(saved.getCategory());
                        log.setAction("Updated: " + saved.getQuantity() + " unit(s) in " + saved.getCategory());
                        log.setUserId(1L);
                        logRepository.save(log);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(item -> {
                    repository.delete(item);
                    
                    // Write global inventory log!
                    try {
                        InventoryLog log = new InventoryLog();
                        log.setItemName(item.getItemName());
                        log.setCategory(item.getCategory());
                        log.setAction("Removed/Consumed: " + item.getItemName());
                        log.setUserId(1L);
                        logRepository.save(log);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/expiring")
    public List<PantryItem> getExpiring() {
        List<PantryItem> items = repository.findAll();
        items.forEach(PantryItem::calculateStatus);
        return items.stream()
                .filter(item -> "Expiring".equals(item.getStatus()) || "Expired".equals(item.getStatus()))
                .collect(Collectors.toList());
    }
}
