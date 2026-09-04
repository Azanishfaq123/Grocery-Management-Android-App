package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem; // Import MenuItem for back button handling
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nssoftwares.com.storemanagment.Connection.ConnectionClass;

public class Stock extends AppCompatActivity {

    private EditText editTextBarcode, editTextitemName, edititemcode, edittextqty;
    private TextView stockQtyTextView;
    Connection con;
    Statement stmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextBarcode = findViewById(R.id.editTextBarcode);
        editTextitemName = findViewById(R.id.editTextitemName);
        stockQtyTextView = findViewById(R.id.StockQty);
        edititemcode = findViewById(R.id.editTextitemCode);
        edittextqty = findViewById(R.id.editTextQty);

        // Set up barcode scanner trigger
        editTextBarcode.setOnClickListener(v -> startBarcodeScanner());

        // Add text changed listener to barcode EditText
        editTextBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform database query when barcode is entered
                if (s.length() > 0) {
                    fetchStockInfo(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // Method to start the barcode scanner
    private void startBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(Stock.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use the default camera
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    // Handle barcode scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                editTextBarcode.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Method to fetch stock info based on barcode
    private void fetchStockInfo(String barcode) {
        con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
        if (con != null) {
            try {
                // Step 1: Fetch total quantity from Stock
                String queryStock = "SELECT SUM(Qty) AS TotalQty, ItemCode, ItemName FROM Stock WHERE Barcode = ? GROUP BY ItemCode, ItemName";
                PreparedStatement stmtStock = con.prepareStatement(queryStock);
                stmtStock.setString(1, barcode);
                ResultSet rsStock = stmtStock.executeQuery();

                int totalQty = 0;
                String itemCode = null;
                String itemName = null;

                if (rsStock.next()) {
                    totalQty = rsStock.getInt("TotalQty");
                    itemCode = rsStock.getString("ItemCode");
                    itemName = rsStock.getString("ItemName");
                } else {
                    // If no stock is found, fetch item name from Itemstbl
                    fetchItemName(barcode);
                    return; // Exit the function as there's no stock to process
                }

                rsStock.close();
                stmtStock.close();

                // Step 2: Fetch picked quantity from VRecipiePick with DISTINCT RecipeCode and ItemCode
                String queryPickedQty = "SELECT COALESCE(SUM(Quantity), 0) AS PickedQty FROM " +
                        "(SELECT DISTINCT RecipeCode, ItemCode, Quantity FROM VRecipiePick " +
                        "WHERE Barcode = ? AND ItemCode = ?) AS DistinctPicked";
                PreparedStatement stmtPicked = con.prepareStatement(queryPickedQty);
                stmtPicked.setString(1, barcode);
                stmtPicked.setString(2, itemCode);
                ResultSet rsPicked = stmtPicked.executeQuery();

                int pickedQty = 0;
                if (rsPicked.next()) {
                    pickedQty = rsPicked.getInt("PickedQty");
                }
                rsPicked.close();
                stmtPicked.close();

                // Step 3: Calculate remaining quantity
                int remainingQty = totalQty - pickedQty;

                // Step 4: Update UI elements
                edititemcode.setText(itemCode);
                editTextitemName.setText(itemName);

                if (remainingQty > 0) {
                    stockQtyTextView.setText("Your current stock: " + remainingQty);
                } else {
                    stockQtyTextView.setText("No stock available after picking");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to fetch item name from itemtbl
    private void fetchItemName(String barcode) {
        try {
            String queryFetchItemName = "SELECT ItemName, ItemCode FROM Itemstbl WHERE Barcode = '" + barcode + "'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(queryFetchItemName);

            if (rs.next()) {
                String itemName = rs.getString("ItemName");
                String itemCode = rs.getString("ItemCode");

                edititemcode.setText(itemCode);
                editTextitemName.setText(itemName);
                stockQtyTextView.setText("No stock available");
            } else {
                Toast.makeText(this, "No item found for this barcode", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveItem(View view) {
        String ItemCode = edititemcode.getText().toString().trim();
        String itemName = editTextitemName.getText().toString().trim();
        String QtyStr = edittextqty.getText().toString().trim();
        String barcode = editTextBarcode.getText().toString().trim();

        if (itemName.isEmpty() || QtyStr.isEmpty() || barcode.isEmpty() || ItemCode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQty;
        try {
            newQty = Integer.parseInt(QtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            con = connectionClass(ConnectionClass.un, ConnectionClass.pass, ConnectionClass.db, ConnectionClass.ip);
            if (con == null) {
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the item already exists in stock
            String queryCheckStock = "SELECT Qty FROM Stock WHERE Barcode = '" + barcode + "'";
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(queryCheckStock);

            if (rs.next()) {
                // If record found, update the quantity
                int existingQty = rs.getInt("Qty");
                int updatedQty = existingQty + newQty;

                String updateSql = "UPDATE Stock SET Qty = " + updatedQty + " WHERE Barcode = '" + barcode + "'";
                stmt.executeUpdate(updateSql);
                Toast.makeText(this, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                // If no record found, insert a new item
                String insertSql = "INSERT INTO Stock (ItemCode, ItemName, Qty, Barcode) VALUES ('" + ItemCode + "', '" + itemName + "', " + newQty + ", '" + barcode + "')";
                stmt.executeUpdate(insertSql);
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
            }

            clearFields(); // Clear all input fields

        } catch (SQLException e) {
            Log.e("SQL Error", "Error executing SQL statement: " + e.getMessage());
            Toast.makeText(this, "Error Saving Item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("General Error", "Error Saving Item: " + e.getMessage());
            Toast.makeText(this, "Error Saving Item", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextBarcode.setText("");
        edittextqty.setText("");
        edititemcode.setText("");
        editTextitemName.setText("");
        stockQtyTextView.setText("0");
    }

    // Method for SQL Server connection
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // Close this activity and return to the previous one
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
