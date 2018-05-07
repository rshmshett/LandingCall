package com.example.rshms.v2landingcall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;

/**
 * Created by rshms on 4/25/2018.
 */

public class AlertSettings extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static String phoneNumber;
    private static String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_set);
    }


    public void btnSubmit_clicked(View view) {
        phoneNumber = ((EditText)findViewById(R.id.txtPhone)).getText().toString();
        Message = ((EditText)findViewById(R.id.txtMessage)).getText().toString();
        String filename = "contactdetails";
        String fileContents = phoneNumber+" " + Message;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(getApplicationContext(),"Emergency Information Saved!",Toast.LENGTH_LONG);
        toast.show();
        //Intent i = new Intent(getApplicationContext(), StartMonitor.class);
        //i.putExtra("Phone", phoneNumber);
        //i.putExtra("Msg", Message);
        //startActivity(i);
        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void btnBack_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void btnTest_clicked(View view) {
        phoneNumber = ((EditText)findViewById(R.id.txtPhone)).getText().toString();
        Message = ((EditText)findViewById(R.id.txtMessage)).getText().toString();
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        try {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, Message, null, null);
            Toast toast = Toast.makeText(getApplicationContext(),"Test Message Sent!",Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }
}
