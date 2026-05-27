package edu.cit.belen.pantrypulse.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/suggest")
    public List<Map<String, Object>> suggestRecipes() {
        List<Recipe> publishedRecipes = recipeRepository.findAll().stream()
                .filter(r -> "Published".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());

        return publishedRecipes.stream().map(recipe -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", recipe.getId());
            map.put("title", recipe.getTitle());
            map.put("description", "A tasty preparation utilizing " + recipe.getTitle().toLowerCase() + "!");
            map.put("image", "https://picsum.photos/300/200?random=" + recipe.getId());
            map.put("ingredients", recipe.getKeyIngredients() != null ? recipe.getKeyIngredients() : "");
            map.put("instructions", recipe.getInstructions() != null ? recipe.getInstructions() : "");
            return map;
        }).collect(Collectors.toList());
    }
}
