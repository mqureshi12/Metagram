package com.example.metagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("O2sJfeuAw5pSUoVFZ0AkoVHDcpYBQbGKUBtj0Byk")
                .clientKey("MKeop6F1PNRtHrTuAn3oHx5sOcupIIAeHYtN0Oup")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
