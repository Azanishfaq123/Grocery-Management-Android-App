package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.Toast;

import nssoftwares.com.storemanagment.Connection.Item;
import nssoftwares.com.storemanagment.Connection.ItemAdapter;
import nssoftwares.com.storemanagment.Connection.ItemLoadOnReceipies;

public class recipiesactivity extends AppCompatActivity {
    private EditText editItemCode, editBarcode, editQty,ReceipeCode,RecipieName;
    private Spinner itemNameSpinner;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private ArrayList<ItemLoadOnReceipies> receipiesItemList;
    private Connection con;
    private Statement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipiesactivity2);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Recipies");

        // Enable the back button on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        editItemCode = findViewById(R.id.editItemCodee);
        editBarcode = findViewById(R.id.editBarcode); // New field for barcode
        itemNameSpinner = findViewById(R.id.itemNameSpinner);
        editQty = findViewById(R.id.editQtyy);
        ReceipeCode = findViewById(R.id.editRecipeCodee);
        recyclerView = findViewById(R.id.recyclerView);
        RecipieName = findViewById(R.id.editRecipeName);
        // Initialize the list and adapter
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        // Fetch data for spinner
        fetchItemData();
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            // Correct connection string format
            connectionURL = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        } catch (Exception e) {
            Log.e("SQL Connection Error", e.getMessage());
        }
        return connection;
    }


    private void fetchItemData() {
        receipiesItemList = new ArrayList<>();

        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";

        try {
            // Establish connection
            con = connectionClass(user, password, database, server);


            if (con == null) {
                Log.e("Database Error", "Connection failed. Please check the connection parameters.");
                Toast.makeText(this, "Error: Database connection failed", Toast.LENGTH_SHORT).show();
                return; // Exit the method if connection fails
            }

            // Log success message
            Log.i("Database Connection", "Connection established successfully.");

            // SQL query
            String sql = "SELECT ItemCode, ItemName, Barcode FROM Itemstbl";
            Log.i("SQL Query", sql); // Log SQL query for debugging

            // Create Statement and Execute Query
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Log.i("SQL Execution", "Query executed successfully, processing results...");

            receipiesItemList.clear(); // Clear the list before adding new data

            // Loop through result set and add items to the list
;            while (rs.next()) {
                String itemCode = rs.getString("ItemCode");
                String itemName = rs.getString("ItemName");
                String barcode = rs.getString("Barcode");

                receipiesItemList.add(new ItemLoadOnReceipies(itemCode, itemName, barcode));
            }

            // Close result set and statement after use
            rs.close();
            stmt.close();
            con.close();
            Log.i("Database", "Connection closed successfully after fetching data.");

            // Set spinner adapter
            ArrayAdapter<ItemLoadOnReceipies> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, receipiesItemList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemNameSpinner.setAdapter(adapter);

            // Spinner item selected listener
            itemNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // Fetch selected item
                    ItemLoadOnReceipies selectedItem = receipiesItemList.get(position);

                    // Set item code and barcode in respective fields
                    editItemCode.setText(selectedItem.getItemCode());
                    editBarcode.setText(selectedItem.getBarcode());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Handle case when nothing is selected if necessary
                }
            });

        } catch (SQLException e) {
            // Handle SQL exceptions
            Log.e("SQL Error", "Error during SQL operation: " + e.getMessage());
            Toast.makeText(this, "SQL Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle general exceptions
            Log.e("Database Error", "Error fetching data: " + e.getMessage());
            Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button click
        if (item.getItemId() == android.R.id.home) {
            finish();  // Finish the activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void additem(View view) {
        String itemCode = editItemCode.getText().toString();
        String itemName = ((ItemLoadOnReceipies) itemNameSpinner.getSelectedItem()).getItemName();
        String qtyText = editQty.getText().toString();
        String barcode = editBarcode.getText().toString(); // Get barcode from EditText

        // Check for empty fields
        if (itemCode.isEmpty() || itemName.isEmpty() || qtyText.isEmpty() || barcode.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity, please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new item and add to the list
        Item newItem = new Item(itemCode, itemName, quantity, barcode); // Include barcode
        itemList.add(newItem);

        // Notify the adapter about the new item
        itemAdapter.notifyItemInserted(itemList.size() - 1);

        // Clear input fields
        editItemCode.setText("");
        editBarcode.setText(""); // Clear barcode field
        editQty.setText("");
    }

    public void savenow(View view) {

      }

    public void SaveRightNow(View view) {
        String recipeName = RecipieName.getText().toString();
        if (recipeName==null) {
            Toast.makeText(this, "Please enter recipe name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare to save items
        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";

        try {
            // Establish database connection
            con = connectionClass(user, password, database, server);

            if (con == null) {
                Log.e("Database Error", "Connection failed. Please check the connection parameters.");
                Toast.makeText(this, "Error: Database connection failed", Toast.LENGTH_SHORT).show();
                return; // Exit the method if connection fails
            }
            String maxCodeQuery = "SELECT MAX(RecipeCode) AS MaxRecipieCode FROM Recipes";
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(maxCodeQuery);
            String newItemCode = "1"; // Default to 1 if no items exist

            if (rs.next()) {
                String maxCodeStr = rs.getString("MaxRecipieCode");
                if (maxCodeStr != null) {
                    int maxCode = Integer.parseInt(maxCodeStr);
                    newItemCode = String.valueOf(maxCode + 1); // Increment max code by 1
                }
            }

            // Set the new ItemCode in the EditText
            ReceipeCode.setText(newItemCode); // Update the ItemCode field
            // Prepare SQL insert statement
            String insertSQL = "INSERT INTO Recipes (RecipeCode, RecipeName, ItemCode, ItemName, Quantity, Barcode) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);

            // Iterate through the item list and insert each item
            for (Item item : itemList) {
                preparedStatement.setString(1, newItemCode);
                preparedStatement.setString(2, recipeName);
                preparedStatement.setString(3, item.getItemCode());
                preparedStatement.setString(4, item.getItemName());
                preparedStatement.setInt(5, item.getQuantity());
                preparedStatement.setString(6, item.getBarcode());

                // Execute the insert
                preparedStatement.executeUpdate();
            }

            // Clean up
            preparedStatement.close();
            con.close();
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();

            // Clear the item list and notify adapter
            itemList.clear();
            itemAdapter.notifyDataSetChanged();

            // Optionally, clear the EditText fields
            editItemCode.setText("");
            editBarcode.setText("");
            editQty.setText("");
            RecipieName.setText("");
            RecipieName.setText("");
        } catch (SQLException e) {
            Log.e("SQL Error", "Error during SQL operation: " + e.getMessage());
            Toast.makeText(this, "SQL Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Database Error", "Error saving data: " + e.getMessage());
            Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
