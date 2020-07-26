package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.loginscreen.signin.SignInActivity;
import com.example.loginscreen.signin.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SlidingSplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuthIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_splash_screen);
        mAuthIn = FirebaseAuth.getInstance();
        Button signupButton = findViewById(R.id.signup_splash);
        Button loginButton = findViewById(R.id.login_splash);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuthIn.getCurrentUser();

        //If user is not null, means already signed in then take to Main Activity
        if (currentUser != null) {
            Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
            startActivity(i);
        }

    }
}