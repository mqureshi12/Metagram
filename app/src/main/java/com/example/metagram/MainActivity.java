package com.example.metagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.metagram.Fragments.ComposeFragment;
import com.example.metagram.Fragments.FeedFragment;
import com.example.metagram.Fragments.ProfileFragment;
import com.example.metagram.Login.LoginActivity;
import com.example.metagram.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String TAG = "MainActivity";
    public BottomNavigationView bottomNavigationView;
    private Button btnLogout;

    FeedFragment feedFragment = new FeedFragment();
    ComposeFragment composeFragment = new ComposeFragment(MainActivity.this);
    ProfileFragment profileFragment = new ProfileFragment(ParseUser.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = feedFragment;
                        break;
                    case R.id.action_compose:
                        fragment = composeFragment;
                        break;
                    case R.id.action_profile:
                    default:
                        profileFragment.userToFilterBy = (User) ParseUser.getCurrentUser();
                        fragment = profileFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private void logoutUser() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                {
                    Log.e(TAG, "Issue with logout", e);
                    Toast.makeText(MainActivity.this, "Issue with logout", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void returnToFeed() {
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    public void goToProfileTab(User user) {
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        profileFragment.userToFilterBy = user;
    }
}