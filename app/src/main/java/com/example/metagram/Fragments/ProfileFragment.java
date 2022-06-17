package com.example.metagram.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.metagram.Adapters.ProfileAdapter;
import com.example.metagram.Models.Post;
import com.example.metagram.Adapters.PostsAdapter;
import com.example.metagram.Models.User;
import com.example.metagram.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment {

    public static final String TAG = "ProfileFragment";
    protected SwipeRefreshLayout swipeContainer;
    public User userToFilterBy;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    protected RecyclerView rvProfile;
    public ImageView ivProfile;
    public TextView tvProfile;

    public ProfileFragment(ParseUser userToFilerBy) {
        this.userToFilterBy = (User) userToFilerBy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts();
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvProfile = view.findViewById(R.id.rvProfile);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvProfile = view.findViewById(R.id.tvProfile);
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        rvProfile.setAdapter(adapter);
        rvProfile.setLayoutManager(new GridLayoutManager(getContext(), 3));

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        userToFilterBy.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                userToFilterBy = (User) object;
                displayUserInfo();
            }
        });
        queryPosts();
   }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKED_BY);
        query.whereEqualTo(Post.KEY_USER, userToFilterBy);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " +
                            post.getUser().getUsername());
                }
                adapter.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void displayUserInfo() {
        tvProfile.setText(userToFilterBy.getUsername());
        ParseFile profilePhoto = userToFilterBy.getProfilePhoto();
        if(profilePhoto != null) {
            Glide.with(getContext()).load(profilePhoto.getUrl()).circleCrop().into(ivProfile);
        } else {
            Glide.with(getContext()).load(R.drawable.default_avatar).circleCrop().into(ivProfile);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // By this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Glide.with(getContext()).load(takenImage).circleCrop().into(ivProfile);
                userToFilterBy.setProfilePhoto(new ParseFile(photoFile));
                userToFilterBy.saveInBackground();
            } else {
                // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}