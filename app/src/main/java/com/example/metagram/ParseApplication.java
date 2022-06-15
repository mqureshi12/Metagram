package com.example.metagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("O2sJfeuAw5pSUoVFZ0AkoVHDcpYBQbGKUBtj0Byk")
                .clientKey("MKeop6F1PNRtHrTuAn3oHx5sOcupIIAeHYtN0Oup")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
