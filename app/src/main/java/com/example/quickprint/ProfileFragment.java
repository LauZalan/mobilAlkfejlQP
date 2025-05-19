package com.example.quickprint;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseUser user;
    private EditText UserName;
    private TextView Email;
    private EditText Pass;
    private EditText Pass2;

    private static final String TAG = ProfileFragment.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference users;

    private User newUser;
    private String email;
    private String id;

    private String pass;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        UserName = view.findViewById(R.id.editTextUserName);
        Pass = view.findViewById(R.id.editTextPassword);
        Pass2 = view.findViewById(R.id.editTextPasswordAgain);
        Email = view.findViewById(R.id.editTextEmail);
        TextView Forbidden = view.findViewById(R.id.Forbidden);
        Button button1 = view.findViewById(R.id.buttonEdit);
        Button button2 = view.findViewById(R.id.logout);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getEmail() == null) {
            UserName.setVisibility(View.GONE);
            Pass.setVisibility(View.GONE);
            Pass2.setVisibility(View.GONE);
            Email.setVisibility(View.GONE);
            Forbidden.setVisibility(View.VISIBLE);
            button1.setVisibility(View.GONE);
        }

        view.findViewById(R.id.buttonEdit).setOnClickListener(v -> update(v));

        getProfileData();
    }

    public void update(View view) {

        if (UserName.getText().toString().isEmpty() || Pass.getText().toString().isEmpty() || Pass2.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all the fields!",Toast.LENGTH_LONG).show();
        }
        else {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), Pass.getText().toString());
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    users.document(id).update("userName", UserName.getText().toString());
                    user.updatePassword(Pass2.getText().toString())
                            .addOnSuccessListener(aVoid -> {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "id")
                                        .setSmallIcon(R.mipmap.ic_launcher_round)
                                        .setContentTitle("Credentials changed!")
                                        .setContentText("Your user credentials have been changed.")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                Random random = new Random();
                                int id = random.nextInt(1000);
                                notificationManager.notify(id, builder.build());
                                Toast.makeText(requireContext(), "Your user credentials have been changed.",Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(requireContext(), "Ooops something went worng!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void logout(View view) {

        ((HomeActivity) requireActivity()).logout(view);

    }

    public void refresh() {
        getProfileData();
    }

    public void getProfileData() {
        mFirestore = FirebaseFirestore.getInstance();
        users = mFirestore.collection("Users");

        Log.i(TAG, "users: " + users.toString());

        Query getUserCred = users.whereEqualTo("email", user.getEmail());

        getUserCred.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i(TAG, document.getId() + " => " + document.getData());
                        String email = document.getString("email");
                        String userName = document.getString("userName");
                        Log.i(TAG, "result email:" + email);
                        Log.i(TAG, "result user:" + userName);
                        id = document.getId();
                        UserName.setText(userName);
                        Email.setText(email);
                        Log.i(TAG, UserName.getText().toString());
                        Log.i(TAG, Email.getText().toString());
                    }
                } else {
                    Log.i(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}