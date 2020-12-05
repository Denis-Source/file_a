package com.example.file_a;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AnalysisActivity extends AppCompatActivity {
    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 1;
    TextView filePathView;
    TextView infoView;
    ImageView imageView;
    Bitmap image;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);
        filePathView = findViewById(R.id.file_path);
        infoView = findViewById(R.id.info);
        imageView = findViewById(R.id.output_image);

        Intent aActivity = getIntent();

        String filePath = getFilePath(Uri.parse(aActivity.getStringExtra("file_uri")));
        filePathView.setText(filePath);

        image = Utils.heatMap(filePath);

        imageView.setImageBitmap(image);
    }
    public String getFilePath(Uri fileUri){
        return Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" +
                fileUri.getPath().split(":")[1];
    }

    public void onSaveImageClick(View view) {
        if (!checkPermissionForWriteExtertalStorage()) {
            try {
                requestPermissionForWriteExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File folder = new File(Environment.getExternalStorageDirectory() +
                "/" + "Pictures" + "/" + "FileA");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            String imageName = Calendar.getInstance().getTimeInMillis() + ".png";
            String imagePath = folder + "/" + imageName;
            try (FileOutputStream out = new FileOutputStream(imagePath)) {
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(this, imageName + " is saved!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPermissionForWriteExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public void requestPermissionForWriteExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}