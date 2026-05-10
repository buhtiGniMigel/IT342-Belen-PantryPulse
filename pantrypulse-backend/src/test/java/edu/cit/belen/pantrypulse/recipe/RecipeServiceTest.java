package edu.cit.belen.pantrypulse.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRecipes_ReturnsList() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Sinangag");
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> result = recipeService.getAllRecipes();
        assertEquals(1, result.size());
        assertEquals("Sinangag", result.get(0).getTitle());
    }

    @Test
    void testCreateRecipe_SavesRecipe() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Adobo");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe saved = recipeService.createRecipe(recipe);
        assertNotNull(saved);
        assertEquals("Adobo", saved.getTitle());
    }

    @Test
    void testDeleteRecipe_DeletesById() {
        doNothing().when(recipeRepository).deleteById(1L);
        recipeService.deleteRecipe(1L);
        verify(recipeRepository, times(1)).deleteById(1L);
    }
}
