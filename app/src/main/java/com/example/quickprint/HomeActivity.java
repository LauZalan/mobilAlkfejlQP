package com.example.quickprint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth auth;

    private FirebaseFirestore mFirestore;
    private CollectionReference users;

    private String email;
    private String userName;
    private String tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment printFragment = new PrintFragment();
        Fragment profileFragment = new ProfileFragment();

        ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile");

        if (fragment != null) {
            fragment.refresh();
        }

        if (savedInstanceState != null) {
            tag = savedInstanceState.getString("current_fragment");
            Fragment restoredFragment = getSupportFragmentManager().findFragmentByTag(tag);
        } else {
            tag = "home";
            setCurrentFragment(homeFragment, "home");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                setCurrentFragment(homeFragment, "home");
            } else if (id == R.id.profile) {
                if (fragment != null) {
                    fragment.refresh();
                }
                setCurrentFragment(profileFragment, "profile");
            } else if (id == R.id.print) {
                setCurrentFragment(printFragment,"print");
            }
            return true;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "my_channel_id", // Channel ID
                    "My Channel",     // Channel Name
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("This is my app's notification channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


    }

    private void setCurrentFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment, tag)
                .commit();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_fragment", tag);
    }
}