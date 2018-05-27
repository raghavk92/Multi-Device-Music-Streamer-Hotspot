package com.example.raj.streamerhotspoter;

import android.app.Application;

/**
 * Created by Raghav on 12/7/2015.
 */
public class applicationwide extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

       /* if(player.thread or service active)
        then dont start server thread
        */

    }
}
