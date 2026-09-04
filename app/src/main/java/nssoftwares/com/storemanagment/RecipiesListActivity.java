package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import nssoftwares.com.storemanagment.Connection.DatabaseHelper;
import nssoftwares.com.storemanagment.Connection.Recipe;
import nssoftwares.com.storemanagment.Connection.RecipeAdapter;

public class RecipiesListActivity extends AppCompatActivity {

    private Connection connection;  // Global connection variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipies_list);

        // Initialize the database connection
        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";
        connection = connectionClass(user, password, database, server);  // Establish the database connection

        if (connection == null) {
            Log.e("Database Connection", "Failed to establish connection.");
        }

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch recipes and ingredients from the database
        DatabaseHelper databaseHelper = new DatabaseHelper();
        List<Recipe> recipeList = databaseHelper.getAllRecipes();

        // Set up the adapter with the recipe data
        RecipeAdapter recipeAdapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(recipeAdapter);
    }

    // Method to delete a recipe when delete button is clicked
    public void deletenow(View view, String recipeCode) {
        try {
            // Call the method to delete the recipe from the database
            deleteRecipeFromDatabase(recipeCode);

            // Set up RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recipesRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Fetch recipes and ingredients from the database
            DatabaseHelper databaseHelper = new DatabaseHelper();
            List<Recipe> recipeList = databaseHelper.getAllRecipes();

            // Set up the adapter with the recipe data
            RecipeAdapter recipeAdapter = new RecipeAdapter(recipeList);
            recyclerView.setAdapter(recipeAdapter);

        } catch (Exception e) {
            Log.e("DeleteError", "Error deleting recipe: " + e.getMessage());
        }
    }

    // Method to establish the database connection
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

    // Method to delete a recipe from the database
    private void deleteRecipeFromDatabase(String recipeCode) {
        if (connection == null) {
            Log.e("Database Error", "Connection is null");
            return;  // Avoid further execution if connection is not established
        }

        String query = "DELETE FROM Recipes WHERE RecipeCode = ?";
        PreparedStatement statement = null;

        try {
            // Prepare the SQL statement
            statement = connection.prepareStatement(query);
            statement.setString(1, recipeCode);

            // Execute the delete query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                Log.d("Database", "Recipe deleted successfully");
            } else {
                Log.d("Database", "Recipe not found or not deleted");
            }

        } catch (SQLException e) {
            Log.e("Database Error", "Error executing delete: " + e.getMessage());

        } finally {
            // Clean up the resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    Log.e("Database Error", "Error closing statement: " + e.getMessage());
                }
            }
        }
    }
}
