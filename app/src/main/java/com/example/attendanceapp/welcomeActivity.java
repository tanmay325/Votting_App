package com.example.attendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class welcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        // Display the welcome screen for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After 2 seconds, start the MainActivity
                Intent intent = new Intent(welcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the WelcomeActivity so the user can't return to it
            }
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
