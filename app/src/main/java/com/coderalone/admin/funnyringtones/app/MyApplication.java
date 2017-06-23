package com.coderalone.admin.funnyringtones.app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class called when the application is starting, before any other application objects have been created.
 */
public class MyApplication extends Application {

    private RequestQueue mRequestQueue;
    private static MyApplication mInstance = null;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * Get RequestQueue to add request URL.
     *
     * @return RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

}
