package nssoftwares.com.storemanagment.Connection;

import java.util.List;

public class Recipe {
    private String name;
    private String Recipecode;

    private List<Ingredient> ingredients;

    public Recipe(String Recipecode,String name, List<Ingredient> ingredients) {
        this.name = name;
        this.Recipecode = Recipecode;

        this.ingredients = ingredients;
    }

    // Getter for recipe name
    public String getName() {
        return name;
    }
    public String getRecipecodee() {
        return Recipecode;
    }

    // Setter for recipe name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for the list of ingredients
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    // Setter for the list of ingredients
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
