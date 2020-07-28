package com.example.loginscreen.ui.slideshow;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.loginscreen.R;
import com.example.loginscreen.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SlideshowFragment extends Fragment {

    private static final String TAG = "Slideshow";
    ArrayList<ModelItems> items = new ArrayList <>();
    SlideshowViewModel adapter;
    Context context;
    int pos;
    String name;
    private FirebaseAuth mAuth;
    String user_id;
    DatabaseReference dbRef;
    RecyclerView recyclerView;

    public SlideshowFragment(){

    }
    public SlideshowFragment(int i){
        this.pos = i;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_slideshow, container, false);
        mAuth=FirebaseAuth.getInstance();
        user_id= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        dbRef =FirebaseDatabase.getInstance().getReference("users");
        dbRef=dbRef.child(user_id);
        recyclerView = root.findViewById(R.id.recyclerViewNews);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User news=snapshot.getValue(User.class);
                assert news != null;
                name=news.getUserHobby();
                String urltemp = "";
                switch (name) {
                    case "Music":
                        //music url
                        urltemp = "https://newsapi.org/v2/everything?q=music&language=en&pageSize=100&apiKey=""";
                        break;
                        //sports url
                    case "Sports":
                        urltemp = "https://newsapi.org/v2/everything?q=sports&language=en&pageSize=100&apiKey=""";
                        break;
                        //games url
                    case "Games":
                        urltemp = "https://newsapi.org/v2/everything?q=games&language=en&pageSize=100&apiKey=""";
                        break;
                        //science url
                    case "Science":
                        urltemp = "https://newsapi.org/v2/everything?q=science&language=en&pageSize=100&apiKey=""";
                        break;
                        //book url
                    case "Books":
                        urltemp = "https://newsapi.org/v2/everything?q=books&language=en&pageSize=100&apiKey=""";
                        break;
                        //films
                    case "Films":
                        urltemp = "https://newsapi.org/v2/everything?q=films&language=en&pageSize=100&apiKey=""";
                        break;
                }
                Log.d(TAG, "onData: "+urltemp);
                Ion.with(getActivity()).load("GET", urltemp).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        String status = result.get("status").getAsString();
                        if (status.equals("ok")) {

                            JsonArray array = result.get("articles").getAsJsonArray();

                            for (int i = 0; i < array.size(); i++) {
                                JsonObject object = array.get(i).getAsJsonObject();

                                String author = object.get("author").toString();

                                String title = object.get("title").toString();
                                title = title.substring(1, title.length() - 1);

                                String url = object.get("url").toString();
                                url = url.substring(1, url.length() - 1);

                                String urlToImage = object.get("urlToImage").toString();
                                urlToImage = urlToImage.substring(1, urlToImage.length() - 1);

                                String date = object.get("publishedAt").toString();
                                String content = object.get("content").toString();
                                content = content.substring(1, content.length() - 1);

                                items.add(new ModelItems(title, author, date, urlToImage, url));
                            }

                            adapter = new SlideshowViewModel(getActivity(), items);
                            recyclerView.setAdapter(adapter);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(layoutManager);
                            Log.d(TAG, "success");
                        } else {
                            Log.e(TAG, "error");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
