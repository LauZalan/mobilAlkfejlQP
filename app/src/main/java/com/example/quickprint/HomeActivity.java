package com.example.quickprint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class HomeActivity extends AppCompatActivity implements DocumentAdapter.OnDocumentActionListener {

    private FirebaseUser user;

    private FirebaseFirestore mFirestore;
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

        if (secret_key == 420) {
            setCurrentFragment(printFragment, "print");
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
                    "id", // Channel ID
                    "Channel",     // Channel Name
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Description");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    public void logout(View view) {
        if (user.getEmail() == null) {
            user.delete();
        }
        Toast.makeText(HomeActivity.this, "Logged out!",Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_fragment", tag);
    }

    @Override
    public void onPrintAgain() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Document printed!")
                .setContentText("Your document has been printed.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Random random = new Random();
        int id = random.nextInt(1000);
        notificationManager.notify(id, builder.build());
        Toast.makeText(this, "Document printed!",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.printFragment);

        if (currentFragment instanceof PrintFragment) {
            PrintFragment docFragment = (PrintFragment) currentFragment;
            DocumentAdapter adapter = docFragment.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onDelete(Document doc) {
        mFirestore = FirebaseFirestore.getInstance();
        CollectionReference docs = mFirestore.collection("Documents");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.printFragment);

        docs.document(doc.getId()).delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Document deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete: " + doc.getName(), Toast.LENGTH_SHORT).show();
                });
        if (currentFragment instanceof PrintFragment) {
            PrintFragment docFragment = (PrintFragment) currentFragment;
            DocumentAdapter adapter = docFragment.getAdapter();
            if (adapter != null) {
                adapter.removeItem(doc);
                adapter.notifyDataSetChanged();
            }

        }
        this.recreate();
    }
}