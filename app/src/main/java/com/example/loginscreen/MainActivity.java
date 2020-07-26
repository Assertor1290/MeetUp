package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    private Button hobbiesContinueButton;
    private EditText user, description;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        hobbiesContinueButton = findViewById(R.id.hobbiesContinueButton);
        user = findViewById(R.id.user);
        description = findViewById(R.id.desc);
        spinner = findViewById(R.id.hobbies);
        dbRef = firebaseDatabase.getReference("users");
        VerifyUserExistence();
    }


    public void init() {
        hobbiesContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = user.getText().toString().trim();
                String desc = description.getText().toString().trim();
                String hobby = spinner.getSelectedItem().toString();
                String image = "default";
                String thumbnail = "default";
                String deviceAppUID = "default";

                if (!TextUtils.isEmpty(name)) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert currentUser != null;
                    String userId = currentUser.getUid();

                    User user = new User(name, hobby, desc, image, thumbnail, deviceAppUID);
                    dbRef.child(userId).setValue(user);
                    startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //to check is user already exists
    public void VerifyUserExistence() {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNameRef = rootRef.child("users").child(currentUserID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    init();
                } else {
                    startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }


}
