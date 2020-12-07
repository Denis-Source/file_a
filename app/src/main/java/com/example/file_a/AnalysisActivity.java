package com.example.file_a;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

public class AnalysisActivity extends AppCompatActivity {
    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 1;

    TextView titleView;
    TextView filePathView;
    TextView infoView;
    ImageView imageView;
    Bitmap image;
    GraphView graphView;
    String method;
    Button saveBtn;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_analisis);

        titleView = findViewById(R.id.analysis_title);
        filePathView = findViewById(R.id.file_path);
        infoView = findViewById(R.id.info);
        imageView = findViewById(R.id.output_image);
        graphView = findViewById(R.id.graph);
        saveBtn = findViewById(R.id.save_btn);

        titleView.setText(method);
        Intent aActivity = getIntent();
        method = aActivity.getStringExtra("method");
        Uri fileUri = Uri.parse(aActivity.getStringExtra("file_uri"));
        InputStream inputStream = null;
        try {
            inputStream = this.getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        saveBtn.setVisibility(View.GONE);
        if (inputStream != null) {
            filePathView.setText(fileUri.getPath());
            switch (method) {
                case "HeatMap":
                    drawHeatMap(inputStream);
                    saveBtn.setVisibility(View.VISIBLE);
                    break;
                case "Bit Distribution":
                    plotBarGraph(Utils.countBits(inputStream));
                    break;
                case "Byte Distribution":
                    plotBarGraph(Utils.countBytes(inputStream));
                    break;
                case "Find Series (length 2)":
                    plotBarGraph(Utils.countSeries(inputStream, 2));
                    break;
                case "Find Series (length 3)":
                    plotBarGraph(Utils.countSeries(inputStream, 3));
                    break;
                case "Find Series (length 4)":
                    plotBarGraph(Utils.countSeries(inputStream, 4));
                    break;
                case "Find Series (length 5)":
                    plotBarGraph(Utils.countSeries(inputStream, 5));
                    break;
                case "Find Series (length 6)":
                    plotBarGraph(Utils.countSeries(inputStream, 6));
                    break;
                case "Find Series (length 7)":
                    plotBarGraph(Utils.countSeries(inputStream, 7));
                    break;
                case "Find Series (length 8)":
                    plotBarGraph(Utils.countSeries(inputStream, 8));
                    break;
            }
        }
    }


    public void plotBarGraph(long[] distribution) {
        imageView.setVisibility(View.GONE);
        try {
            DataPoint[] dataPoints;
            dataPoints = new DataPoint[distribution.length];

            for (int i = 0; i < dataPoints.length; i++) {
                dataPoints[i] = new DataPoint(i + 1, distribution[i]);
            }
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
            series.setSpacing(50);
            series.setAnimated(true);
            series.setColor(Color.RED);
            graphView.addSeries(series);
            graphView.getViewport().setScalable(true);
            graphView.getViewport().setMinX(0d);
            graphView.getViewport().setMaxX(4d);
            graphView.getViewport().setXAxisBoundsManual(true);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void drawHeatMap(InputStream inputStream) {
        graphView.setVisibility(View.GONE);
        image = Utils.heatMap(inputStream);
        imageView.setImageBitmap(image);
    }

    public void onSaveImageClick(View view) {
        if (!checkPermissionForWriteExternalStorage()) {
            try {
                requestPermissionForWriteExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File folder = new File(
                String.format("%s/Pictures/FileA",
                        Environment.getExternalStorageDirectory()));

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

    public boolean checkPermissionForWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForWriteExternalStorage() {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}