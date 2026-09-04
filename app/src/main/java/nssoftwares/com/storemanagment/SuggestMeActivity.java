package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.database.SQLException;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nssoftwares.com.storemanagment.Connection.SuggestMeAdapter;

public class SuggestMeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SuggestMeAdapter suggestMeAdapter;
    private List<String> availableRecipes;

    // Database connection details
    private final String user = "sa";
    private final String password = "nssol789.";
    private final String database = "storeandroid";
    private final String server = "185.239.239.178";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_me);

        Log.d("SuggestMeActivity", "onCreate started");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        availableRecipes = new ArrayList<>();
        suggestMeAdapter = new SuggestMeAdapter(this, availableRecipes);
        recyclerView.setAdapter(suggestMeAdapter);

        // Check for available recipes
        try {
            checkAllRecipesAvailability();
            Log.d("SuggestMeActivity", "Checked all recipe availability");
        } catch (Exception e) {
            Log.e("SuggestMeActivity", "Error checking recipes: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading recipes. Please try again.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if there's an error
        }
    }

    // Check for recipe availability in stock
    private void checkAllRecipesAvailability() {
        try (Connection connection = connectionClass(user, password, database, server)) {
            if (connection != null) {
                String recipeQuery = "SELECT RecipeCode, RecipeName, ItemCode, Quantity FROM Recipes";
                try (Statement stmt = connection.createStatement(); ResultSet recipeSet = stmt.executeQuery(recipeQuery)) {

                    Map<String, String> recipeNames = new HashMap<>();
                    Map<String, List<Map.Entry<String, Integer>>> recipeIngredients = new HashMap<>();

                    while (recipeSet.next()) {
                        String recipeCode = recipeSet.getString("RecipeCode");
                        String recipeName = recipeSet.getString("RecipeName");
                        String itemCode = recipeSet.getString("ItemCode");
                        int requiredQty = recipeSet.getInt("Quantity");

                        recipeNames.put(recipeCode, recipeName);
                        recipeIngredients.putIfAbsent(recipeCode, new ArrayList<>());
                        recipeIngredients.get(recipeCode).add(new AbstractMap.SimpleEntry<>(itemCode, requiredQty));
                    }

                    for (Map.Entry<String, List<Map.Entry<String, Integer>>> recipeEntry : recipeIngredients.entrySet()) {
                        String recipeCode = recipeEntry.getKey();
                        boolean allIngredientsAvailable = true;

                        for (Map.Entry<String, Integer> ingredientEntry : recipeEntry.getValue()) {
                            String itemCode = ingredientEntry.getKey();
                            int requiredQty = ingredientEntry.getValue();

                            // Log item code and required quantity
                            Log.d("Check Recipes0", "Checking stock for ItemCode: " + itemCode + ", Required Qty: " + requiredQty);

                            // Fetch the available quantity for the current item code
                            String stockQuery = "SELECT COALESCE(SUM(Qty), 0) AS TotalQty FROM Stock WHERE ItemCode = ?";
                            try (PreparedStatement stockStmt = connection.prepareStatement(stockQuery)) {
                                stockStmt.setString(1, itemCode);
                                try (ResultSet stockSet = stockStmt.executeQuery()) {
                                    if (stockSet.next()) {
                                        int totalAvailableQty = stockSet.getInt("TotalQty");
                                        Log.d("Stock Query", "Total available quantity for " + itemCode + ": " + totalAvailableQty);
                                        if (totalAvailableQty <= requiredQty) {
                                            allIngredientsAvailable = false;
                                            Log.d("Check Recipes", "Not enough stock for: " + itemCode);
                                            break; // No need to check further if one ingredient is missing
                                        }
                                        else {
                                            allIngredientsAvailable = true;

                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                Log.e("Stock Query Error", "Error fetching stock for itemCode: " + itemCode, e);
                                allIngredientsAvailable = false; // Set flag to false on error
                                break; // Exit the ingredient loop on error
                            }
                        }

                        // If all ingredients are available, add the recipe to availableRecipes
                        if (allIngredientsAvailable) {
                            availableRecipes.add(recipeNames.get(recipeCode));
                            Log.d("Available Recipe", "Recipe available: " + recipeNames.get(recipeCode));
                        }
                    }

                    // Notify adapter that data has changed
                    suggestMeAdapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    Log.e("Recipe Query Error", "Error executing recipe query", e);
                }
            } else {
                Log.e("Database Error", "Connection is null");
            }
        } catch (Exception e) {
            Log.e("Recipe Check Error", "Error checking recipe availability", e);
            e.printStackTrace();
        }
    }

    // Database connection method
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
            Log.e("SQL Connection Error", "Failed to connect to database", e);
        }
        return connection;
    }

    // Event handler for the "Pick" button
    public void onPick(View view) {
        String recipeName = (String) view.getTag();
        String recipeCode = getRecipeCodeByName(recipeName);
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if (savePickedRecipe(recipeCode, recipeName, dateTime)) {
            Toast.makeText(this, "Recipe Picked Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error Picking Recipe", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to retrieve recipe code based on the recipe name
    private String getRecipeCodeByName(String recipeName) {
        String recipeCode = "";
        String query = "SELECT RecipeCode FROM Recipes WHERE RecipeName = ?";

        try (Connection connection = connectionClass(user, password, database, server);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, recipeName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                recipeCode = resultSet.getString("RecipeCode");
            }
            resultSet.close();
        } catch (SQLException | java.sql.SQLException e) {
            Log.e("Database Error", "Error retrieving recipe code", e);
        }
        return recipeCode;
    }

    // Method to save picked recipe details into PickedRecipes table
    private boolean savePickedRecipe(String recipeCode, String recipeName, String dateTime) {
        boolean isSuccess = false;
        String insertQuery = "INSERT INTO PickedRecipes (RecipeCode, RecipeName, DateTime) VALUES (?, ?, ?)";

        try (Connection connection = connectionClass(user, password, database, server);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, recipeCode);
            preparedStatement.setString(2, recipeName);
            preparedStatement.setString(3, dateTime);

            isSuccess = preparedStatement.executeUpdate() > 0;
        } catch (SQLException | java.sql.SQLException e) {
            Log.e("Database Error", "Error saving picked recipe", e);
        }
        return isSuccess;
    }
}
