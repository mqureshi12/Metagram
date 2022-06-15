package com.example.metagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";

    Post post;
    private EditText etCompose;
    private Button btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        post = getIntent().getParcelableExtra("post");
        etCompose = findViewById(R.id.etCompose);
        btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etCompose.getText().toString();
                Comment comment = new Comment();
                comment.setAuthor(ParseUser.getCurrentUser());
                comment.setBody(body);
                comment.setPost(post);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e(TAG, e.getMessage());
                            return;
                        }
                        finish();
                    }
                });
            }
        });
    }
}