package com.example.loginscreen.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginscreen.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//A view model represents the data that you want to display on your view/page
public class HomeViewModel extends RecyclerView.ViewHolder {

    TextView userSingleName,userSingleDescription;
    ImageView imageView;
    //used for setting on click listener of recycler view items
    //and setting values for them
    View mView;
    public HomeViewModel(@NonNull View itemView) {
        super(itemView);
        mView=itemView;
    }

    public void setName(String userName) {
        userSingleName=itemView.findViewById(R.id.users_single_name);
        userSingleName.setText(userName);
    }

    public void setDesc(String userDesc) {
        userSingleDescription=itemView.findViewById(R.id.users_single_description);
        userSingleDescription.setText(userDesc);
    }

    public void setImage(String userImage) {
        imageView=itemView.findViewById(R.id.users_single_image);
        if(!userImage.equals("default")){
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(imageView);
        }
    }
}
