package com.example.quickprint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        TextView textViewBig = findViewById(R.id.yay);
        TextView textViewSmall = findViewById(R.id.oops);
        Button button = findViewById(R.id.logout);

        if (secret_key == 99) {
            textViewBig.setText("Successful Login!");
            textViewSmall.setText("This place is under construction :/ You can sign out with this cool button though:");
            button.setVisibility(View.VISIBLE);
        }

        if (user != null) {
            Log.i(TAG, "Yay!");

            if (user.getEmail() == null) {
                Toast.makeText(HomeActivity.this, "Logged in as: Anonymous",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(HomeActivity.this, "Logged in as: " + user.getEmail(),Toast.LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "Ooops!");
            finish();
        }

        drawerLayout = findViewById(R.id.main);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Synchronize the toggle's state with the linked DrawerLayout
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // Called when an item in the NavigationView is selected.
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle the selected item based on its ID
                if (item.getItemId() == R.id.nav_account) {
                    // Show a Toast message for the Account item
                    Toast.makeText(HomeActivity.this,
                            "Account Details", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.nav_settings) {
                    // Show a Toast message for the Settings item
                    Toast.makeText(HomeActivity.this,
                            "Settings Opened", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.nav_logout) {
                    // Show a Toast message for the Logout item
                    Toast.makeText(HomeActivity.this,
                            "You are Logged Out", Toast.LENGTH_SHORT).show();
                }

                // Close the drawer after selection
                drawerLayout.closeDrawers();
                // Indicate that the item selection has been handled
                return true;
            }
        });

        // Add a callback to handle the back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Called when the back button is pressed.
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Finish the activity if the drawer is closed
                    finish();
                }
            }
        });
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }

    public void logout(View view) {
        if (user.getEmail() == null) {
            user.delete();
        }
        Toast.makeText(HomeActivity.this, "Logged out!",Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}