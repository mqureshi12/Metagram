package com.example.metagram.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.metagram.Adapters.ProfileAdapter;
import com.example.metagram.Models.Post;
import com.example.metagram.Adapters.PostsAdapter;
import com.example.metagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    protected SwipeRefreshLayout swipeContainer;
    protected ParseUser userToFilterBy;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    protected RecyclerView rvProfile;
    private ImageView ivProfile;
    private TextView tvProfile;

    public ProfileFragment(ParseUser userToFilerBy) {
        this.userToFilterBy = userToFilerBy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvProfile = view.findViewById(R.id.rvProfile);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvProfile = view.findViewById(R.id.tvProfile);
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        rvProfile.setAdapter(adapter);
        int numberOfColumns = 3;
        rvProfile.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        Glide.with(getContext()).load(R.drawable.default_avatar).circleCrop().into(ivProfile);
        tvProfile.setText(ParseUser.getCurrentUser().getUsername());
        queryPosts();

//        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
//        swipeContainer = view.findViewById(R.id.swipeContainer);
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i(TAG,"Fetching profile posts data");
//                adapter.clear();
//                allPosts.clear();
//                queryPosts();
//            }
//        });
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
//                swipeContainer.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
