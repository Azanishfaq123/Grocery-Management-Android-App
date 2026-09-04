package nssoftwares.com.storemanagment.Connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private Connection connection;

    // Constructor to initialize connection with the provided credentials
    public DatabaseHelper() {
        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";
        connection = connectionClass(user, password, database, server);
    }

    // Method to create and return the connection
    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            Log.e("SQL Connection Error", e.getMessage());
        }
        return connection;
    }

    // Method to fetch all recipes
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT DISTINCT RecipeCode, RecipeName FROM Recipes ORDER BY RecipeCode";  // Fetch distinct recipes

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String recipeName = resultSet.getString("RecipeName");
                String recipeCode = resultSet.getString("RecipeCode");

                // Fetch ingredients for this distinct recipe code
                List<Ingredient> ingredients = getIngredientsByRecipeCode(recipeCode);

                // Add recipe to the list
                recipes.add(new Recipe(recipeCode,recipeName, ingredients));
            }

        } catch (SQLException e) {
            Log.e("Database Error", e.getMessage());
        }

        return recipes;
    }

    // Method to fetch ingredients for a particular recipe
    private List<Ingredient> getIngredientsByRecipeCode(String recipeCode) {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT * FROM Recipes WHERE RecipeCode = ?";  // Modify based on your schema

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, recipeCode);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String itemName = resultSet.getString("ItemName");
                int quantity = resultSet.getInt("Quantity");
                String barcode = resultSet.getString("Barcode");

                ingredients.add(new Ingredient(itemName, quantity, barcode));
            }

        } catch (SQLException e) {
            Log.e("Database Error", e.getMessage());
        }

        return ingredients;
    }
}
