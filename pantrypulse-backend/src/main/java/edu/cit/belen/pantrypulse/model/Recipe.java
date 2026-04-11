package edu.cit.belen.pantrypulse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String keyIngredients;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private String status; // "Draft" or "Published"
    private String author = "Admin";

    // --- Standard Getters and Setters (Replaces Lombok @Data) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getKeyIngredients() { return keyIngredients; }
    public void setKeyIngredients(String keyIngredients) { this.keyIngredients = keyIngredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}