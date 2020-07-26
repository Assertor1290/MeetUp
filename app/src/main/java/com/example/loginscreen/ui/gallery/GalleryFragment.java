package com.example.loginscreen.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loginscreen.ChatActivity;
import com.example.loginscreen.R;
import com.example.loginscreen.User;
import com.example.loginscreen.ui.home.HomeViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class GalleryFragment extends Fragment {

    private static final String TAG ="GalleryFragment" ;
    private GalleryViewModel galleryViewModel;
    private RecyclerView recyclerView;
    private DatabaseReference allFriendsRef;
    private DatabaseReference allUserRef;
    private FirebaseAuth mAuth;
    String userId;

    //empty constructor
    public GalleryFragment(){

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options=new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(allFriendsRef,Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends,GalleryViewModel> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Friends, GalleryViewModel>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GalleryViewModel holder, int position, @NonNull Friends model) {

                allFriendsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //if to prevent crash in case no user exists
                        if(snapshot.exists()){
                            //the code in this dataChange is used to display user details of friends
                            String view_friends_id= getRef(position).getKey();
                            assert view_friends_id != null;
                            allUserRef.child(view_friends_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String user_name= Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                                    String display_description= Objects.requireNonNull(snapshot.child("userDesc").getValue()).toString();
                                    String display_image= Objects.requireNonNull(snapshot.child("userImage").getValue()).toString();

                                    holder.setName(user_name);
                                    holder.setDesc(display_description);
                                    holder.setImage(display_image);

                                    //to open chat activity
                                    holder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent  intent=new Intent(getActivity(), ChatActivity.class);
                                            intent.putExtra("user_id",view_friends_id);
                                            intent.putExtra("user_Name",user_name);
                                            intent.putExtra("user_image",display_image);
                                            startActivity(intent);

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        //////////////////////////////////////////////////
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public GalleryViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout,parent,false);
                return new GalleryViewModel(view);
            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mAuth=FirebaseAuth.getInstance();
        userId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        allFriendsRef= FirebaseDatabase.getInstance().getReference("Friends").child(userId);
        allUserRef= FirebaseDatabase.getInstance().getReference("users");
        recyclerView = root.findViewById(R.id.recyclerViewFriends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}