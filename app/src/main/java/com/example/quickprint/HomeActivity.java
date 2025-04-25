package com.example.quickprint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private FirebaseUser user;
    private FirebaseAuth auth;

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

        TextView textViewBanner = findViewById(R.id.textViewQuickPrintLogin);
        TextView textViewBig = findViewById(R.id.yay);
        TextView textViewSmall = findViewById(R.id.oops);
        Button button = findViewById(R.id.logout);

        if (secret_key == 99) {
            textViewBanner.setText("Yay!");
            textViewBig.setText("Successful Login!");
            textViewSmall.setText("This place is under contruction :/ You can sign out with this cool button though:");
            button.setVisibility(View.VISIBLE);
        }

        if (user != null) {

            Log.i(TAG, "Yay!");
            Toast.makeText(HomeActivity.this, "Logged in as: " + user.getEmail().toString(),Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "Ooops!");
            finish();
        }
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }

    public void logout(View view) {
        Toast.makeText(HomeActivity.this, "Logged out!",Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}