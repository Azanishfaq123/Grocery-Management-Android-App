package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import nssoftwares.com.storemanagment.Connection.ConnectionClass;
import nssoftwares.com.storemanagment.Connection.ExistingItemAdapter;
import nssoftwares.com.storemanagment.Connection.FetchExistingItem;

public class ItemActivity extends AppCompatActivity {
    EditText ItemCode, ItemName, Minqty, Barcode; // Added Barcode EditText
    RecyclerView recyclerView;
    ExistingItemAdapter adapter;
    List<FetchExistingItem> itemList = new ArrayList<>();
    String selectedItemCode = null; // To keep track of the selected item for update/delete
    Connection con;
    Statement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        ItemCode = findViewById(R.id.editTextitemCode);
        ItemName = findViewById(R.id.editTextitemName);
        Minqty = findViewById(R.id.editTextminQty);
        Barcode = findViewById(R.id.editTextBarcode); // Initialize Barcode EditText
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Barcode EditText to launch barcode scanner when clicked
        Barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode(); // Launch barcode scanner
            }
        });

        // Fetch and load items into RecyclerView
        fetchAndLoadItems();

        // Set the adapter for RecyclerView
        adapter = new ExistingItemAdapter(itemList, new ExistingItemAdapter.OnItemDoubleClickListener() {
            @Override
            public void onItemDoubleClick(FetchExistingItem item) {
                // Populate EditText fields with the double-clicked item data
                selectedItemCode = item.getItemCode(); // Keep track of the selected item code
                ItemCode.setText(item.getItemCode());
                ItemName.setText(item.getItemName());
                Minqty.setText(String.valueOf(item.getMinQty()));
                Barcode.setText(item.getBarcode()); // Populate barcode
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // Fetch items from database and load into RecyclerView
    private void fetchAndLoadItems() {
        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";

        try {
            con = connectionClass(user, password, database, server);

            if (con == null) {
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                Log.e("Database Connection", "Connection is null. Check your connection details.");
            } else {
                Log.i("Database Connection", "Connection Successful");

                // SQL Query
                String sql = "SELECT ItemCode, ItemName, MinQty, Barcode FROM Itemstbl"; // Include Barcode in SELECT
                Log.i("SQL Query", sql); // Log SQL query for debugging

                // Create Statement and Execute Query
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                Log.i("SQL Execution", "Query executed successfully, processing results...");

                itemList.clear(); // Clear the list before adding new data

                // Iterate over the result set
                while (rs.next()) {
                    String itemCode = rs.getString("ItemCode");
                    String itemName = rs.getString("ItemName");
                    int minQty = rs.getInt("MinQty");
                    String barcode = rs.getString("Barcode"); // Fetch barcode

                    Log.i("Fetched Data", "ItemCode: " + itemCode + ", ItemName: " + itemName + ", MinQty: " + minQty + ", Barcode: " + barcode);

                    // Add the fetched item to the list
                    itemList.add(new FetchExistingItem(itemCode, itemName, minQty, barcode)); // Include barcode in the constructor
                }

                // Notify adapter that data has changed
                adapter.notifyDataSetChanged(); // IMPORTANT: This tells the adapter to refresh the data
                Log.i("Adapter", "Data loaded and adapter notified.");
            }
        } catch (SQLException e) {
            Log.e("SQL Error", "Error during query execution: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "SQL Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("General Error", "General error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveItem(View view) {
        String itemName = ItemName.getText().toString().trim();
        String minQtyStr = Minqty.getText().toString().trim();
        String barcode = Barcode.getText().toString().trim();

        if (itemName.isEmpty() || minQtyStr.isEmpty() || barcode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int minQty;
        try {
            minQty = Integer.parseInt(minQtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Minimum quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
            if (con == null) {
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if we are inserting a new item or updating an existing one
            if (selectedItemCode == null) { // Insert new item
                // Fetch the maximum ItemCode from the database
                String maxCodeQuery = "SELECT MAX(ItemCode) AS MaxItemCode FROM Itemstbl";
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(maxCodeQuery);
                String newItemCode = "1"; // Default to 1 if no items exist

                if (rs.next()) {
                    String maxCodeStr = rs.getString("MaxItemCode");
                    if (maxCodeStr != null) {
                        int maxCode = Integer.parseInt(maxCodeStr);
                        newItemCode = String.valueOf(maxCode + 1); // Increment max code by 1
                    }
                }

                // Set the new ItemCode in the EditText
                ItemCode.setText(newItemCode); // Update the ItemCode field

                // Prepare the SQL insert statement
                String sql = "INSERT INTO Itemstbl (ItemCode, ItemName, MinQty, Barcode) VALUES ('" + newItemCode + "', '" + itemName + "', " + minQty + ", '" + barcode + "')";
                stmt.executeUpdate(sql);
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
            } else { // Update existing item
                String sql = "UPDATE Itemstbl SET ItemName = '" + itemName + "', MinQty = " + minQty + ", Barcode = '" + barcode + "' WHERE ItemCode = '" + selectedItemCode + "'";
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT).show();
            }
            clearFields(); // Clear all input fields
            fetchAndLoadItems(); // Refresh the RecyclerView after saving the item
        } catch (SQLException e) {
            Log.e("SQL Error", "Error executing SQL statement: " + e.getMessage());
            Toast.makeText(this, "Error Saving Item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("General Error", "Error Saving Item: " + e.getMessage());
            Toast.makeText(this, "Error Saving Item", Toast.LENGTH_SHORT).show();
        }

    }

    // Delete an item
    public void deleteItem(View view) {
        if (selectedItemCode == null) {
            Toast.makeText(this, "Please select an item to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
            if (con == null) {
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
            } else {
                String sql = "DELETE FROM Itemstbl WHERE ItemCode = '" + selectedItemCode + "'";
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                Toast.makeText(this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                clearFields(); // Clear the input fields after deleting
                fetchAndLoadItems(); // Refresh the RecyclerView after deleting the item
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Error Deleting Item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Clear all input fields
    private void clearFields() {
        ItemCode.setText("");
        ItemName.setText("");
        Minqty.setText("");
        Barcode.setText("");
        selectedItemCode = null; // Reset the selected item code
    }

    // Handle the "Back" button in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Launch the barcode scanner using ZXing library
    private void scanBarcode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode");
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    // Handle the result from the barcode scanner
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            Barcode.setText(result.getContents()); // Set the scanned barcode value to the Barcode EditText
            Toast.makeText(ItemActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    });

    // Create a database connection
    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException | ClassNotFoundException e) {
            Log.e("Database Connection", e.getMessage());
        }

        return connection;
    }

    public void Newclear(View view) {
        clearFields();
    }
}
