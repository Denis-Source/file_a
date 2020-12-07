package com.example.file_a;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICKFILE_RESULT_CODE = 1;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 2;

    String[] methods = {
            "HeatMap",
            "Bit Distribution",
            "Byte Distribution",

    };
    String currentMethod;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        spinner = findViewById(R.id.method_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, methods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSelectFileClick(View view) {
        if (!checkPermissionForReadExternalStorage()) {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("*/*");
        try {
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri filePath = data.getData();
                Intent aActivity = new Intent(this, AnalysisActivity.class);
                aActivity.putExtra("file_uri", filePath.toString());
                aActivity.putExtra("method", currentMethod);
                startActivity(aActivity);
            }
        }
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentMethod = methods[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Select method!", Toast.LENGTH_SHORT).show();

    }
}