package com.example.quickprint;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

public class PrintActivity extends AppCompatActivity {

    private static final String TAG = PrintActivity.class.getName();

    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channelId",
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Description");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

    }

    public void select(View view) {
        openPDFPicker();
    }

    private void openPDFPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            String fileName = System.currentTimeMillis() + ".pdf";
            //uploadPDFToFirebase(pdfUri);
            //Storage is not included in free plan, don't want to bother with other providers
            //saving only metadata to database :/
            savePDFMetadataToFirestore(fileName, pdfUri.toString());
        }
    }

    private void uploadPDFToFirebase(Uri pdfUri) {
        if (pdfUri == null) return;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = System.currentTimeMillis() + ".pdf";
        StorageReference fileRef = storageRef.child("pdfs/" + fileName);

        fileRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        savePDFMetadataToFirestore(fileName, downloadUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void savePDFMetadataToFirestore(String fileName, String downloadUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Document document = new Document(fileName, downloadUrl, user.getEmail());

        db.collection("Documents")
                .add(document)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "PDF uploaded and saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Firestore save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void back(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY", 420);
        startActivity(intent);
        this.finish();
    }
}