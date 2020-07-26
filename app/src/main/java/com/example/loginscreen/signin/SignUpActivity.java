package com.example.loginscreen.signin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginscreen.MainActivity;
import com.example.loginscreen.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignUpActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText mEmail, mPassword, mName, mPhone;
    CheckBox mCheckBox;
    Button mSignup;
    TextView mSignin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mToolbar = findViewById(R.id.toolbar);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mCheckBox = findViewById(R.id.checkbox);
        mSignup = findViewById(R.id.signup);
        mSignin = findViewById(R.id.signin);

        mAuth = FirebaseAuth.getInstance();

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is required");
                    mName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone is required");
                    mPhone.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    mEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password must be >=6 characters");
                    mPassword.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        //take to sign in screen
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

}
