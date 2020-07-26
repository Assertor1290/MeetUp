package com.example.loginscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView profileName, profileDesc, profileFriends;
    ImageView imageProfileActivity;
    Button btnSendRequest, btnDeclineRequest;
    private DatabaseReference dbReference, mFriendRequestRef, mFriendAcceptRef, mNotificationDatabase;
    //fr friend request feature
    private String mCurrent_State;
    //this to get our own id
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //this user id is id of person whom we are viewing
        String user_id = getIntent().getStringExtra("User id");
        assert user_id != null;
        dbReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        //creating another object
        mFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //this one to list all friends
        mFriendAcceptRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        //new child for notification
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");
        profileName = findViewById(R.id.textProfileName);
        profileDesc = findViewById(R.id.textProfileDescription);
        btnSendRequest = findViewById(R.id.sendRequest);
        btnDeclineRequest = findViewById(R.id.declineRequest);
        imageProfileActivity = findViewById(R.id.profileImageView);

        mCurrent_State = "not_friends";

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String display_name = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                String display_description = Objects.requireNonNull(snapshot.child("userDesc").getValue()).toString();
                String display_image = Objects.requireNonNull(snapshot.child("userImage").getValue()).toString();
                profileName.setText(display_name);
                profileDesc.setText(display_description);
                if (!display_image.equals("default")) {
                    Picasso.get().load(display_image).placeholder(R.drawable.avatar).into(imageProfileActivity);
                }

                mFriendRequestRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(user_id)) {
                            String req_type = Objects.requireNonNull(snapshot.child(user_id).child("request_type").getValue()).toString();

                            if (req_type.equals("received")) {

                                mCurrent_State = "req_received";
                                btnSendRequest.setText("Accept Friend Request");
                                btnDeclineRequest.setVisibility(View.VISIBLE);
                                btnDeclineRequest.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                mCurrent_State = "req_sent";
                                btnSendRequest.setText("Cancel Friend Request");
                                btnDeclineRequest.setVisibility(View.INVISIBLE);
                                btnDeclineRequest.setEnabled(false);
                            }
                        } else {
                            mFriendAcceptRef.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(user_id)) {

                                        //it means the person is friend already.
                                        mCurrent_State = "friends";
                                        btnSendRequest.setText("Unfriend the Person");
                                        btnDeclineRequest.setVisibility(View.INVISIBLE);
                                        btnDeclineRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------------NOT FRIEND STATE
                btnSendRequest.setEnabled(false);
                //it should check current state and if the other user is not friend it should send
                //him a friend request
                if (mCurrent_State.equals("not_friends")) {
                    mFriendRequestRef.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //if sent successfully, it sets friend request to received in others id
                                mFriendRequestRef.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrentUser.getUid());
                                        notificationData.put("type", "request");
                                        //child to whom we are sending notification
                                        //push create random id
                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrent_State = "req_sent";
                                                btnSendRequest.setText("Cancel Friend Request");
                                                btnDeclineRequest.setVisibility(View.INVISIBLE);
                                                btnDeclineRequest.setEnabled(false);
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Send Request Failed", Toast.LENGTH_SHORT).show();
                            }
                            btnSendRequest.setEnabled(true);
                        }
                    });
                }
                //---------------CANCEL  FRIEND REQUEST
                if (mCurrent_State.equals("req_sent")) {
                    //First delete from our id
                    mFriendRequestRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //then delete us from others id
                            mFriendRequestRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnSendRequest.setEnabled(true);
                                    mCurrent_State = "not_friends";
                                    btnSendRequest.setText("Send Friend Request");
                                    btnDeclineRequest.setVisibility(View.INVISIBLE);
                                    btnDeclineRequest.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //REQUEST RECEIVED STATE--to accept friend request
                if (mCurrent_State.equals("req_received")) {
                    String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    //in our friends list add other users id
                    mFriendAcceptRef.child(mCurrentUser.getUid()).child(user_id).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //also in other users id add our id
                            mFriendAcceptRef.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //now we want to delete friend request sent and received from the other structure
                                    //we change  the state in other structure to friends
                                    //like we deleted data on cancel friend request, we delete data here too

                                    mFriendRequestRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    btnSendRequest.setEnabled(true);
                                                    mCurrent_State = "friends";
                                                    btnSendRequest.setText("Unfriend the Person");
                                                    btnDeclineRequest.setVisibility(View.INVISIBLE);
                                                    btnDeclineRequest.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                //UNFRIEND PERSON
                if (mCurrent_State.equals("friends")) {
                    mFriendAcceptRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendAcceptRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnSendRequest.setEnabled(true);
                                    mCurrent_State = "not_friends";
                                    btnSendRequest.setText("Send Friend Request");
                                    btnDeclineRequest.setVisibility(View.INVISIBLE);
                                    btnDeclineRequest.setEnabled(false);
                                }
                            });
                        }
                    });
                }


            }
        });

        btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendRequestRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //then delete us from others id
                        mFriendRequestRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                btnSendRequest.setEnabled(true);
                                mCurrent_State = "not_friends";
                                btnSendRequest.setText("Send Friend Request");
                                btnDeclineRequest.setVisibility(View.INVISIBLE);
                                btnDeclineRequest.setEnabled(false);
                            }
                        });
                    }
                });
            }
        });
    }
}