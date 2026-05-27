package edu.cit.belen.pantrypulse.admin;

import edu.cit.belen.pantrypulse.user.User;
import edu.cit.belen.pantrypulse.user.UserRepository;
import edu.cit.belen.pantrypulse.inventory.InventoryLog;
import edu.cit.belen.pantrypulse.inventory.InventoryLogRepository;
import edu.cit.belen.pantrypulse.recipe.Recipe;
import edu.cit.belen.pantrypulse.recipe.RecipeRepository;
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
    public Recipe addRecipe(@RequestBody Recipe r) {
        if (r.getStatus() == null) {
            r.setStatus("Published");
        }
        return recipeRepo.save(r);
    }

    @GetMapping("/users")
    public List<User> getUsers() { return userRepo.findAll(); }

    @GetMapping("/logs")
    public List<InventoryLog> getLogs() { return logRepo.findAll(); }
}