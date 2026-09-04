package nssoftwares.com.storemanagment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import nssoftwares.com.storemanagment.Connection.AvailableRecipe;
import nssoftwares.com.storemanagment.Connection.AvailableDatabaseHelper;
import nssoftwares.com.storemanagment.Connection.AvailableRecipeAdapter;

public class AvailableRecipiesActivity extends AppCompatActivity {

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_recipies);

        String user = "sa";
        String password = "nssol789.";
        String database = "storeandroid";
        String server = "185.239.239.178";
        connection = connectionClass(user, password, database, server);

        RecyclerView recyclerView = findViewById(R.id.rvAvailableRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AvailableDatabaseHelper dbHelper = new AvailableDatabaseHelper();
        List<AvailableRecipe> availableRecipeList = dbHelper.getAllAvailableRecipes();

        AvailableRecipeAdapter adapter = new AvailableRecipeAdapter(availableRecipeList);
        recyclerView.setAdapter(adapter);
    }

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
}
