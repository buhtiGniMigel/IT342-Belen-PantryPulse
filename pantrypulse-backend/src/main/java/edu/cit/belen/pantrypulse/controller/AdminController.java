package edu.cit.belen.pantrypulse.controller;

import edu.cit.belen.pantrypulse.model.*;
import edu.cit.belen.pantrypulse.repository.*; // This imports all repositories
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired private RecipeRepository recipeRepo;
    @Autowired private InventoryLogRepository logRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("users", userRepo.count());
        stats.put("recipes", recipeRepo.count());
        stats.put("logs", logRepo.count());
        return stats;
    }

    @GetMapping("/recipes")
    public List<Recipe> getRecipes() { return recipeRepo.findAll(); }

    @PostMapping("/recipes")
    public Recipe addRecipe(@RequestBody Recipe r) { return recipeRepo.save(r); }

    @GetMapping("/users")
    public List<User> getUsers() { return userRepo.findAll(); }

    @GetMapping("/logs")
    public List<InventoryLog> getLogs() { return logRepo.findAll(); }
}