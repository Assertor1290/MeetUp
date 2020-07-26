package com.example.loginscreen.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.loginscreen.ProfileActivity;
import com.example.loginscreen.R;
import com.example.loginscreen.User;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private static final String TAG ="HomeFragment" ;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private DatabaseReference allUserRef;
    private FirebaseAuth mAuth;
    String userId,current_user_hobby;

    //empty constructor
    public HomeFragment(){

    }

    @Override
    public void onStart() {
        super.onStart();
        //from Firebase UI
        //needs model class and viewholder
        //The FirebaseRecyclerAdapter binds a Query to a RecyclerView. When data is added,
        //removed, or changed these updates are automatically applied to your UI in real time.
        //First, configure the adapter by building FirebaseRecyclerOptions
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(allUserRef,User.class)
                .build();

        //Next create the FirebaseRecyclerAdapter object. You should already have a ViewHolder
        //subclass for displaying each item.
        FirebaseRecyclerAdapter<User,HomeViewModel> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<User, HomeViewModel>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final HomeViewModel holder, final int position, @NonNull final User model) {
                // Bind the Chat object to the ChatHolder
                allUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            holder.setName(model.getUserName());
                            holder.setDesc(model.getUserDesc());
                            holder.setImage(model.getUserImage());

                            final String user_id_single = getRef(position).getKey();
                            //mView is like the whole list of a single person in Recycler View
                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                                    profileIntent.putExtra("User id", user_id_single);
                                    startActivity(profileIntent);
                                }
                            });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public HomeViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.users_single_layout for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout,parent,false);
                return new HomeViewModel(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth=FirebaseAuth.getInstance();
        userId= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        allUserRef= FirebaseDatabase.getInstance().getReference("users");
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }
}