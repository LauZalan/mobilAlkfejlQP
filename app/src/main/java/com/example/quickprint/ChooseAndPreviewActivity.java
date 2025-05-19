package com.example.quickprint;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ChooseAndPreviewActivity extends AppCompatActivity {

    private ImageView mImageView;
    private PdfRenderer mPdfRenderer;
    private PdfRenderer.Page mPdfPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_and_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "id",
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

        mImageView = findViewById(R.id.pdf);

        try {
            openPdfWithAndroidSDK(mImageView, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void print(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
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
        back(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPdfPage != null) {
            mPdfPage.close();
        }
        if (mPdfRenderer != null) {
            mPdfRenderer.close();
        }
    }

    void copyToLocalCache(File outputFile, @RawRes int pdfResource) throws IOException {
        if (!outputFile.exists()) {
            InputStream input = getResources().openRawResource(pdfResource);
            FileOutputStream output;
            output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int size;
            while ((size = input.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            input.close();
            output.close();
        }
    }

    void openPdfWithAndroidSDK(ImageView imageView, int pageNumber) throws IOException {
        File fileCopy = new File(getCacheDir(), "lorem_ipsum.pdf");
        copyToLocalCache(fileCopy, R.raw.lorem_ipsum);

        ParcelFileDescriptor fileDescriptor =
                ParcelFileDescriptor.open(fileCopy,
                        ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(fileDescriptor);
        mPdfPage = mPdfRenderer.openPage(pageNumber);

        Bitmap bitmap = Bitmap.createBitmap(mPdfPage.getWidth(),
                mPdfPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        imageView.setImageBitmap(bitmap);
    }

    public void back(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY", 420);
        startActivity(intent);
        this.finish();
    }
}