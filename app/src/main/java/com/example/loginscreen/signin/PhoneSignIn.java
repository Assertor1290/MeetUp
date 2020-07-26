package com.example.loginscreen.signin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.loginscreen.R;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneSignIn extends AppCompatActivity {
    EditText mGetNo;
    Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_in);
        mGetNo = findViewById(R.id.editTextMobile);
        mSubmit = findViewById(R.id.buttonContinue);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mGetNo.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    mGetNo.setError("Phone number required");
                    mGetNo.requestFocus();
                    return;
                }
                if (phone.length() < 10) {
                    mGetNo.setError("Valid Phone number required");
                    mGetNo.requestFocus();
                    return;
                }
                Intent intent = new Intent(PhoneSignIn.this, PhoneVerification.class);
                intent.putExtra("phoneNumber", phone);
                startActivity(intent);
            }
        });
    }
}
