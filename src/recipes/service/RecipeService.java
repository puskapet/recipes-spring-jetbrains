package recipes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.model.Recipe;
import recipes.persistence.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe findRecipeById(Integer id) {
        return recipeRepository.findRecipeById(id);
    }

    public Recipe save(Recipe toSave) {
        return recipeRepository.save(toSave);
    }

    public void deleteRecipeById(Integer id) {
        recipeRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return recipeRepository.existsById(id);
    }

    public List<Recipe> findRecipeByName(String name) {
        return recipeRepository.findByNameContainsIgnoreCaseOrderByDateDesc(name);
    }

    public List<Recipe> findRecipeByCategory(String category) {
        return recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
    }
}
