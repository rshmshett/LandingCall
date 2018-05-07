package com.example.rshms.v2landingcall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void StartMon_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), StartMonitor.class));
    }

    public void StopMon_clicked(View view) {
        Toast toast = Toast.makeText(getApplicationContext(),"LandingFall Monitoring Stopped",Toast.LENGTH_LONG);
        toast.show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void ShowStats_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), ShowStats.class));
    }

    public void AlertSet_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), AlertSettings.class));
    }

}
