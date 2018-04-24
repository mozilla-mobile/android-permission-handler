package org.mozilla.permissionhandler.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    // A sample of permission requesting based on
    // https://developer.android.com/training/permissions/requesting.html

    private static final int STORAGE_REQUEST_CODE = 8899;
    private static final int CREATE_FILE_FAILED = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(MainActivity.this, R.string.need_permission, Toast.LENGTH_LONG).show();
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
                    }
                } else {
                    doCreation();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doCreation();
                } else {
                    Toast.makeText(MainActivity.this, R.string.need_permission, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void doCreation() {
        int fileId = createFile();
        String result;
        if (fileId == CREATE_FILE_FAILED) {
            result = "File creation failed";
        } else {
            result = String.format("File %s created", genFileName(fileId));
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private int createFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        int fileId = 0;
        String fileName = genFileName(fileId);
        File file = new File (root, fileName);
        while (file.exists ()) {
            fileId++;
            fileName = genFileName(fileId);
            file = new File (root, fileName);
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write("PERMISSION_HANDLER\n".getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return CREATE_FILE_FAILED;
        }
        return fileId;
    }

    private String genFileName(int fileId) {
        return "File-"+ fileId +".txt";
    }

}
