package com.example.powerautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        //makes the activity full screen
        //to hide the action bar, view the code in manifest file
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(sharedPreferences.getString("username", "").equals("")){
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }

            }
            //edit the delay below according to your choice
        }, 2000);
    }
}
