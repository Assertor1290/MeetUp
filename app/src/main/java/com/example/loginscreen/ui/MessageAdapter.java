package com.example.loginscreen.ui;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.loginscreen.Messages;
import com.example.loginscreen.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleService;
import androidx.recyclerview.widget.RecyclerView;

//adapter for messages
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference displayImage;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public ImageView profileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText=itemView.findViewById(R.id.message_single_text);
            profileImage=itemView.findViewById(R.id.single_chat);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String current_user_id= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Messages c=mMessageList.get(position);

        String from_user=c.getFrom();
        //to display image while chatting
        displayImage=FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        displayImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("userImage")) {
                    String receiverImage = Objects.requireNonNull(snapshot.child("userImage").getValue()).toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.avatar).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //if we are sending  message
        if(from_user.equals(current_user_id))
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)holder.messageText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, RelativeLayout.TRUE);
            holder.messageText.setLayoutParams(layoutParams);

            holder.messageText.setBackgroundResource(R.drawable.message_side_layout);
            holder.messageText.setTextColor(Color.BLACK);
        }
        //if other person is sending message
        else{
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }
        holder.messageText.setText(c.getMessage());
        //holder.profileImage.setText(c.getType());
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
