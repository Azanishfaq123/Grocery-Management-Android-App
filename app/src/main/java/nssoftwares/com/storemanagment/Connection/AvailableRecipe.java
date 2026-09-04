package nssoftwares.com.storemanagment.Connection;

import java.util.List;

public class AvailableRecipe {
    private String recipeCode;
    private String name;
    private List<AvailableIngredient> ingredients;

    public AvailableRecipe(String recipeCode, String name, List<AvailableIngredient> ingredients) {
        this.recipeCode = recipeCode;
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getRecipeCode() { return recipeCode; }
    public String getName() { return name; }
    public List<AvailableIngredient> getIngredients() { return ingredients; }
}
