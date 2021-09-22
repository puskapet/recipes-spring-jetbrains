package recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class GetRecipeDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    private String date;

    @NotBlank
    private String description;

    @NotEmpty
    private String[] ingredients;

    @NotEmpty
    private String[] directions;


}
