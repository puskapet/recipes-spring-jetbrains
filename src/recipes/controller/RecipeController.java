package recipes.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.auth.AuthenticationFacade;
import recipes.dto.CreateUserDTO;
import recipes.dto.GetRecipeDTO;
import recipes.dto.UpdateRecipeDTO;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.service.AuthService;
import recipes.service.RecipeService;
import recipes.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Configuration
@AllArgsConstructor
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/recipe/{id}")
    public GetRecipeDTO getRecipe(@PathVariable int id) {
        if (recipeService.existsById(id)) {
            Recipe result = recipeService.findRecipeById(id);
            return new GetRecipeDTO(
                    result.getName(),
                    result.getCategory(),
                    result.getDate(),
                    result.getDescription(),
                    result.getIngredients(),
                    result.getDirections());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/recipe/new")
    public String createRecipe(@Valid @RequestBody Recipe recipe) {
        Optional<User> current = userService.findUserByEmail(getCurrentAuthUser());
        Recipe recipeCreate = recipeService.save(
                new Recipe(
                recipe.getName(),
                recipe.getCategory(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getDirections(),
                current.get()
                ));
        return "{\n\"id\": " + recipeCreate.getId() + "\n}";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable int id) {
        if (recipeService.existsById(id)) {
            Recipe recipe = recipeService.findRecipeById(id);
            if (recipe.getCreator().getEmail() == getCurrentAuthUser()) {
                recipeService.deleteRecipeById(id);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(@PathVariable int id,
                             @Valid @RequestBody UpdateRecipeDTO recipe) {
        if (recipeService.existsById(id)) {
            Recipe toUpdate = recipeService.findRecipeById(id);
            if (toUpdate.getCreator().getEmail() == getCurrentAuthUser()) {
                toUpdate.setName(recipe.getName());
                toUpdate.setCategory(recipe.getCategory());
                toUpdate.setDescription(recipe.getDescription());
                toUpdate.setIngredients(recipe.getIngredients());
                toUpdate.setDirections(recipe.getDirections());
                toUpdate.setDate(LocalDateTime.now().toString());
                recipeService.save(toUpdate);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/api/recipe/search")
    public List<GetRecipeDTO> searchRecipe(
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> name) {
        if ((category.isPresent() && name.isPresent()) || (category.isEmpty() && name.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (category.isPresent()) {
            List<Recipe> searchResult = recipeService.findRecipeByCategory(category.get());
            List<GetRecipeDTO> ret = new ArrayList<>();
            for (Recipe r : searchResult) {
                ret.add(new GetRecipeDTO(
                    r.getName(),
                    r.getCategory(),
                    r.getDate(),
                    r.getDescription(),
                    r.getIngredients(),
                    r.getDirections())
                );
            }
            return ret;
        } else {
            List<Recipe> searchResult = recipeService.findRecipeByName(name.get());
            List<GetRecipeDTO> ret = new ArrayList<>();
            for (Recipe r : searchResult) {
                ret.add(new GetRecipeDTO(
                    r.getName(),
                    r.getCategory(),
                    r.getDate(),
                    r.getDescription(),
                    r.getIngredients(),
                    r.getDirections())
                );
            }
            return ret;
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/api/register")
    public void createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        Optional<User> newUser = userService.findUserByEmail(createUserDTO.getEmail());
        if (newUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            User toSave = new User(
                    createUserDTO.getEmail(),
                    createUserDTO.getPassword());
            userService.save(toSave);
        }
    }

    private String getCurrentAuthUser() {
        Authentication auth = authenticationFacade.getAuthentication();
        String current = null;
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            current = auth.getName();
        }

        return current;
    }
}