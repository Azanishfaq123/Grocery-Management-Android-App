package nssoftwares.com.storemanagment.Connection;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvailableDatabaseHelper {

    private Connection connection;

    // Constructor that initializes the database connection
    public AvailableDatabaseHelper() {
        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";
        connection = new DatabaseHelper().connectionClass(user, password, database, server);
    }

    // Fetches all available recipes with their ingredients
    public List<AvailableRecipe> getAllAvailableRecipes() {
        List<AvailableRecipe> availableRecipes = new ArrayList<>();
        String query = "SELECT DISTINCT RecipeCode, RecipeName FROM Recipes ORDER BY RecipeCode";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String recipeCode = resultSet.getString("RecipeCode");
                String recipeName = resultSet.getString("RecipeName");

                // Fetch ingredients for this recipe code
                List<AvailableIngredient> ingredients = getIngredientsByRecipeCode(recipeCode);
                availableRecipes.add(new AvailableRecipe(recipeCode, recipeName, ingredients));
            }

        } catch (SQLException e) {
            Log.e("Database Error", "Error fetching recipes", e);
        }

        return availableRecipes;
    }

    // Fetches ingredients by recipe code and calculates the available quantity
    private List<AvailableIngredient> getIngredientsByRecipeCode(String recipeCode) {
        List<AvailableIngredient> ingredients = new ArrayList<>();
        String query = "SELECT ItemName, Quantity, Barcode, ItemCode " +
                "FROM Recipes WHERE RecipeCode = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recipeCode);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String itemName = resultSet.getString("ItemName");
                int quantity = resultSet.getInt("Quantity");
                String barcode = resultSet.getString("Barcode");
                String itemCode = resultSet.getString("ItemCode");

                // Get the total available quantity from Stock for the current ItemCode
                int availQty = getTotalAvailableQty(itemCode);

                // Add the ingredient with the available quantity
                ingredients.add(new AvailableIngredient(itemName, quantity, barcode, availQty));
            }

        } catch (SQLException e) {
            Log.e("Database Error", "Error fetching ingredients for RecipeCode: " + recipeCode, e);
        }

        return ingredients;
    }

    // Retrieves the total available quantity from Stock for a given ItemCode
    private int getTotalAvailableQty(String itemCode) {
        String query = "SELECT COALESCE(SUM(Qty), 0) AS availQty FROM Stock WHERE ItemCode = ?";
        int availQty = 0;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemCode);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                availQty = resultSet.getInt("availQty");
            }

        } catch (SQLException e) {
            Log.e("Database Error", "Error fetching available quantity for ItemCode: " + itemCode, e);
        } catch (Exception e) {
            Log.e("Unexpected Error", "Unexpected error", e);
        }

        return availQty;
    }
}
