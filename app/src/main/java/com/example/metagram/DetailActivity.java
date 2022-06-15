package com.example.metagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private Post post;
    private TextView tvUsername;
    private TextView tvDescription;
    private ImageView ivImage;
    private ImageButton ibComment;
    private RecyclerView rvComments;
    private ImageButton ibLike;
    private TextView tvLikes;
    private TextView tvTimestamp;
    private CommentsAdapter adapter;
    public static final String TAG = "DetailActivity";
    private List<Comment> allComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.i(TAG, "showing details for post: " + post.getDescription() + ", username: " + post.getUser().getUsername());

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        ibComment = findViewById(R.id.ibComment);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        rvComments = findViewById(R.id.rvComments);
        ibLike = findViewById(R.id.ibLike);
        tvLikes = findViewById(R.id.tvLikes);
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvTimestamp.setText(timeAgo);

        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, CommentsActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(this, allComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        refreshComments();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshComments();
    }

    void refreshComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo(Comment.KEY_POST, post);
        query.orderByDescending("createdAt");
        query.include(Comment.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if(e != null) {
                    Log.e("Failed to get comments", e.getMessage());
                    return;
                }
                adapter.comments.clear();
                allComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}