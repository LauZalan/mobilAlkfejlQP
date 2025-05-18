package com.example.quickprint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    EditText userNameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;

    private FirebaseFirestore mFirestore;
    private CollectionReference users;
    private SharedPreferences preferences;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Bundle bundle = getIntent().getExtras();
        //int secret_key = bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }

        userNameET = findViewById(R.id.editTextUserName);
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordAgainET = findViewById(R.id.editTextPasswordAgain);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        auth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();
        users = mFirestore.collection("Users");

        //userNameET.setText(userName);
        //passwordET.setText(password);
        //passwordAgainET.setText(password);

    }

    public void backToLogin(View view) {
        finish();
    }

    public void Success() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY", 98);
        startActivity(intent);
    }

    public void register(View view) {
        Log.i(TAG, "user: " + userNameET.getText().toString());
        Log.i(TAG, "email: " + emailET.getText().toString());
        Log.i(TAG, "pass: " + passwordET.getText().toString());
        Log.i(TAG, "pass_ag: " + passwordAgainET.getText().toString());

        if (!passwordAgainET.getText().toString().equals(passwordET.getText().toString()) ||
            userNameET.getText().toString().isEmpty() || emailET.getText().toString().isEmpty() ||
            passwordET.getText().toString().isEmpty() || passwordAgainET.getText().toString().isEmpty()) {
            Log.i(TAG, "Oops! Something went wrong!");
            Toast.makeText(RegisterActivity.this, "Oops! Some fields on the registration form are not filled properly :/",Toast.LENGTH_LONG).show();
        } else {
            User user = new User(userNameET.getText().toString(), emailET.getText().toString(), passwordET.getText().toString());
            users.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.i(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Error adding document", e);
                        }
                    });
            auth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.i(TAG, "Success!");
                        Success();
                    } else {
                        Log.i(TAG, "Opps!");
                        Toast.makeText(RegisterActivity.this, "Oops! Something went wrong! " + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}