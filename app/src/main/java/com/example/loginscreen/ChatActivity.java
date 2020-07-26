package com.example.loginscreen;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginscreen.ui.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    //to get data to display stored messages
    private final List<Messages> messageList = new ArrayList<>();
    private DatabaseReference mRootRef;
    private String mCurentUserId;
    private String mChatUser;
    private EditText sendTextArea;
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.friend_chat_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChatUser = getIntent().getStringExtra("user_id");
        String mChatUserName = getIntent().getStringExtra("user_Name");
        String mUserImage = getIntent().getStringExtra("user_image");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_view, null);
        actionBar.setCustomView(action_bar_view);

        TextView mTextView = findViewById(R.id.textNameChat);
        ImageView mImageTop = findViewById(R.id.custom_bar_image);

        mAdapter = new MessageAdapter(messageList);
        recyclerView = findViewById(R.id.recyclerViewChat);
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayout);
        recyclerView.setAdapter(mAdapter);

        //initialise custom chat bar
        mTextView.setText(mChatUserName);
        assert mUserImage != null;
        if (!mUserImage.equals("default")) {
            Picasso.get().load(mUserImage).placeholder(R.drawable.avatar).into(mImageTop);
        }

        ImageView sendButton = findViewById(R.id.send);
        ImageView plusButton = findViewById(R.id.plus);
        sendTextArea = findViewById(R.id.textSend);

        mCurentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = sendTextArea.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "messages/" + mCurentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurentUserId;

            DatabaseReference user_Message_Push = mRootRef.child("messages").child(mCurentUserId).child(mChatUser).push();
            String push_id = user_Message_Push.getKey();

            Map messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", mCurentUserId);

            Map messageUserMap = new HashMap<>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null)
                        Log.d("CHAT_LOG", databaseError.getMessage());
                }
            });
            sendTextArea.setText(" ");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRootRef.child("messages").child(mCurentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message = snapshot.getValue(Messages.class);
                messageList.add(message);
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}