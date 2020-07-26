package com.example.loginscreen.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.loginscreen.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//ViewModel is an Android architecture component. It is used as a data manager in the application.
//A ViewModel provides a way to create and retrieve objects. It typically stores the state of a
//view’s data and communicates with other components.
//ViewModel stores your data for UI and it’s lifecycle-aware.
public class SlideshowViewModel extends RecyclerView.Adapter<SlideshowViewModel.ViewHolder> {
    private final ArrayList<ModelItems> items;
    Context context;

    public SlideshowViewModel(Context context, ArrayList<ModelItems> items) {
        this.context = context;
        this.items = items;
    }

    //this contains layout for single item in recycler view
    @NonNull
    @Override
    public SlideshowViewModel.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.news, null);
        return new ViewHolder(layout);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelItems item = items.get(position);

        String title = item.getTitle();
        String author = item.getAuthor();
        String date = item.getDate();
        String urlToImage = item.getUrlToImage();
        final String url = item.getUrl();

        if (title != null)
            holder.editTitle.setText(title);
        if (author != null)
            holder.editauthor.setText("Author  " + author);
        if (date != null)
            holder.editdate.setText("published at: " + date);
        if (urlToImage != null)
            Picasso.get().load(urlToImage).into(holder.editUrlImage);

        holder.mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView editTitle, editauthor, editdate;
        ImageView editUrlImage;
        Button mybutton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            editTitle = itemView.findViewById(R.id.tittle1);
            editUrlImage = itemView.findViewById(R.id.image1);
            editauthor = itemView.findViewById(R.id.author);
            editdate = itemView.findViewById(R.id.date);
            mybutton = itemView.findViewById(R.id.details);

        }
    }

}
