package edu.cit.belen.pantrypulse.pantry;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "pantry_items")
public class PantryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String expiryDate; // format: YYYY-MM-DD

    private String status; // Fresh, Expiring, Expired

    @PostLoad
    @PrePersist
    @PreUpdate
    public void calculateStatus() {
        if (expiryDate == null || expiryDate.isBlank()) {
            this.status = "Fresh";
            return;
        }
        try {
            LocalDate expiry = LocalDate.parse(expiryDate);
            LocalDate today = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(today, expiry);
            if (daysBetween < 0) {
                this.status = "Expired";
            } else if (daysBetween <= 3) {
                this.status = "Expiring";
            } else {
                this.status = "Fresh";
            }
        } catch (Exception e) {
            this.status = "Fresh";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; this.calculateStatus(); }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
