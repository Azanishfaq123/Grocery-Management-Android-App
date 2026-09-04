package nssoftwares.com.storemanagment;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nssoftwares.com.storemanagment.Connection.GroceryStore;
import nssoftwares.com.storemanagment.Connection.GroceryStoreAdapter;

public class GroceryStoresActivity extends AppCompatActivity {
    private RecyclerView recyclerViewStores;
    private GroceryStoreAdapter adapter;
    private List<GroceryStore> groceryStoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grocery_stores);

        // Enable the Action Bar Up Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Grocery Stores");
        }

        // Set up the RecyclerView
        recyclerViewStores = findViewById(R.id.recyclerViewStores);
        recyclerViewStores.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your list of grocery stores
        groceryStoreList = new ArrayList<>();
        groceryStoreList.add(new GroceryStore("Daraz Shopping Mall", "https://i.brecorder.com/primary/2023/02/0622341446411c3.jpg", "https://www.daraz.pk/"));
        groceryStoreList.add(new GroceryStore("Ali Express", "https://play-lh.googleusercontent.com/WOyaAm4xbMBpo9sgBXTucbtpUiAgwxuq3P7CAf2wwqG7ujMr9CrOdSImk8_2QjhctA", "https://www.aliexpress.com/"));

        groceryStoreList.add(new GroceryStore("Ali Baba", "https://is1-ssl.mzstatic.com/image/thumb/Purple211/v4/a6/c8/e2/a6c8e2e7-3487-315c-27c5-3386651bd32d/AppIcon-0-0-1x_U007epad-0-0-sRGB-85-220.png/1200x600wa.png", "https://www.alibaba.com/"));
        groceryStoreList.add(new GroceryStore("Grocer App", "https://play-lh.googleusercontent.com/ErMdZufSq2p4wu74mk4bYdr1sh1FJppF335KX2jn-mUBxq0EiWw-avEGCs3G99zi8g=w600-h300-pc0xffffff-pd", "https://grocerapp.pk/"));

        // Add more stores as needed

        adapter = new GroceryStoreAdapter(groceryStoreList, this);
        recyclerViewStores.setAdapter(adapter);

        // Handle window insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Handle the Action Bar Up Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back to the previous activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
