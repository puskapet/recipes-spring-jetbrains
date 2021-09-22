package recipes.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import recipes.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

    Recipe findRecipeById(Integer id);
    List<Recipe> findByNameContainsIgnoreCaseOrderByDateDesc(String name);
    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String category);
}
