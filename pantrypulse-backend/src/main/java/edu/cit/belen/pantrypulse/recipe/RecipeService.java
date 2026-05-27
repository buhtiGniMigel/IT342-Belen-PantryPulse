package edu.cit.belen.pantrypulse.recipe;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository repo;
    public RecipeService(RecipeRepository repo) { this.repo = repo; }
    public List<Recipe> getAllRecipes() { return repo.findAll(); }
    public Recipe createRecipe(Recipe recipe) { return repo.save(recipe); }
    public void deleteRecipe(Long id) { repo.deleteById(id); }
}