package org.mozilla.permissionhandler.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.mozilla.permissionhandler.PermissionHandle;
import org.mozilla.permissionhandler.PermissionHandler;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    // A sample of permission requesting based on
    // https://developer.android.com/training/permissions/requesting.html
    // and later rewritten with PermissionHandler

    private static final int CREATE_FILE_FAILED = -1;
    private PermissionHandler permissionHandler;
    private static final int ACTION_CREATE_FILE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionHandler = new PermissionHandler(new PermissionHandle() {
            @Override
            public void doActionDirect(String permission, int actionId, Parcelable params) {
                doCreation();
            }

            @Override
            public void doActionGranted(String permission, int actionId, Parcelable params) {
                doCreation();
            }

            @Override
            public void doActionSetting(String permission, int actionId, Parcelable params) {
                doCreation();
            }

            @Override
            public void doActionNoPermission(String permission, int actionId, Parcelable params) {
                Toast.makeText(MainActivity.this, "User Rejects", Toast.LENGTH_LONG).show();
            }

            @Override
            public int getDoNotAskAgainDialogString(int actionId) {
                return R.string.need_permission;
            }

            @Override
            public Snackbar makeAskAgainSnackBar(int actionId) {
                return PermissionHandler.makeAskAgainSnackBar(MainActivity.this, findViewById(R.id.container), R.string.need_permission);
            }

            @Override
            public void requestPermissions(int actionId) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, actionId);
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionHandler.tryAction(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, ACTION_CREATE_FILE, null);
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        permissionHandler.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        permissionHandler.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionHandler.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
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
