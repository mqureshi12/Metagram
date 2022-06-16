package com.example.metagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.metagram.Adapters.CommentsAdapter;
import com.example.metagram.Models.Comment;
import com.example.metagram.Models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        Log.i(TAG, "Showing details for post: " + post.getDescription() + ", username: " + post.getUser().getUsername());

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

        if(post.isLikedByCurrentUser()) {
            ibLike.setImageResource(R.drawable.ufi_heart_filled);
        } else {
            ibLike.setImageResource(R.drawable.ufi_heart);
        }
        tvLikes.setText(post.getLikesCount());

        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, CommentsActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ParseUser> likedBy = post.getLikedBy();
                if(post.isLikedByCurrentUser()) {
                    // Unlike
                    post.unLike();
                    ibLike.setImageResource(R.drawable.ufi_heart);
                } else {
                    // Like
                    post.like();
                    ibLike.setImageResource(R.drawable.ufi_heart_filled);
                }
                tvLikes.setText(post.getLikesCount());
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
        query.include(Post.KEY_LIKED_BY);
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