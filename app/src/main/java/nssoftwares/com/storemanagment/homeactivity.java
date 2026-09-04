package nssoftwares.com.storemanagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import nssoftwares.com.storemanagment.databinding.ActivityHomeactivity2Binding;

public class homeactivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeactivity2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Correct way to initialize the binding object
        binding = ActivityHomeactivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view using binding

        // Continue with other setup logic...
        setSupportActionBar(binding.appBarHomeactivity.toolbar);
        // Initialize the DrawerLayout and NavigationView
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Retrieve the username and email from the intent passed by MainActivity
        Intent intent = getIntent();

        String email = intent.getStringExtra("Email");

        // Find the header view and update the username and email fields
        View headerView = navigationView.getHeaderView(0);
        TextView txtUsername = headerView.findViewById(R.id.txtUsername);
        TextView txtEmail = headerView.findViewById(R.id.txtEmail);

        // Set the retrieved username and email in the navigation header
        if ( email != null) {
            txtEmail.setText(email);
        } else {
            txtUsername.setText("Guest");
            txtEmail.setText("Not logged in");
        }

        // Setup the FAB (floating action button) event

        // Setup the AppBarConfiguration with the DrawerLayout
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homeactivity);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homeactivity, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homeactivity);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void Items(MenuItem item) {
        Intent intent = new Intent(homeactivity.this, ItemActivity.class);
        startActivity(intent);    }

    public void logout(MenuItem item) {
        Intent intent = new Intent(homeactivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void Recipies(MenuItem item) {
        Intent intent = new Intent(homeactivity.this, recipiesactivity.class);
        startActivity(intent);
    }

    public void grocerystrores(MenuItem item) {
        Intent intent = new Intent(homeactivity.this, GroceryStoresActivity.class);
        startActivity(intent);
    }

    public void suggestMe(View view) {
        Intent intent = new Intent(homeactivity.this, SuggestMeActivity.class);
        startActivity(intent);
    }

    public void CompareAvailableStock(View view) {
        Intent intent = new Intent(homeactivity.this, AvailableRecipiesActivity.class);
        startActivity(intent);
    }
    public void ScanMe(MenuItem item) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode");
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        
    }

}