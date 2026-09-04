package nssoftwares.com.storemanagment.Connection;

import java.util.List;

public class AvalableStockList {
    private String name;
    private String Recipecode;

    private List<Ingredient> ingredients;

    public AvalableStockList(String Recipecode, String name) {
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

}
