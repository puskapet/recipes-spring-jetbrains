package recipes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
public class Recipe {

    public Recipe(String name, String category, String description, String[] ingredients, String[] directions, User creator) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.directions = directions;
        this.category = category;
        this.date = LocalDateTime.now().toString();
        this.creator = creator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    @NotBlank
    private String name;

    @Column
    @NotBlank
    private String category;

    @Column
    @NotBlank
    private String description;

    @Column
    @NotEmpty
    private String[] ingredients;

    @Column
    @NotEmpty
    private String[] directions;

    @Column
    @Size(min=8)
    private String date;

    @ManyToOne
    private User creator;
}
