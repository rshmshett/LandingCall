
package com.example.rshms.v2landingcall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.example.rshms.v2landingcall.Api.URL_CREATE_HERO;

public class StartMonitor extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_mon);
    }

    public void clear_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void connect_clicked(View view) {
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        textResponse = (TextView)findViewById(R.id.response);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        int genid=radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(genid);
        String flooring=radioButton.getText().toString();
        MyClientTask myClientTask = new MyClientTask(editTextAddress.getText().toString(),
                Integer.parseInt(editTextPort.getText().toString()),flooring);
        myClientTask.execute();
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String phoneNumber;
        String Message;
        String response = "";
        String flooring="";
        Socket socket = null;

        MyClientTask(String Txtaddr, int Txtport,String floor){
            dstAddress = Txtaddr;
            dstPort = Txtport;
            flooring = floor;
        }

        @Override
        public Void doInBackground(Void... arg0) {
            //Bundle extras = getIntent().getExtras();
            //if (extras != null) {
            //    phoneNumber = extras.getString("Phone");
            //    Message = extras.getString("Msg");
            //}
            //Log.d("Phone", phoneNumber);
            //Log.d("Msg", Message);
            String filename = "contactdetails";
            //Read file in Internal Storage
            FileInputStream fis;
            String content = "";
            try {
                fis = openFileInput(filename);
                byte[] input = new byte[fis.available()];
                while (fis.read(input) != -1) {
                }
                content += new String(input);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String both[]= content.split(" ");
            phoneNumber = both[0];
            Message = both[1];
            Log.d("Phone", phoneNumber);
            Log.d("Msg", Message);
            try
            {
                socket = new Socket(dstAddress, dstPort);
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    response = "";
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response = byteArrayOutputStream.toString("UTF-8");
                    Log.d("resonse", response);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    String event = currentDateandTime;
                    //Calling the create API
                    Api.URL_CREATE_HERO = Api.URL_CREATE_HERO+"&event="+event+"&deviceip="+dstAddress;

                    PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_HERO,null, CODE_GET_REQUEST);
                    request.execute();


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
                        Log.d("damar","gone");
                    }

                }


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{/*
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
}
