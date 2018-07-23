package com.example.guilherme.mobe.app;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mResquestQueue;

    private static AppController mInstance;

    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() { return mInstance; }

    public RequestQueue getRequestQueue(){
        if(mResquestQueue == null){
            mResquestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mResquestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){

        req.setRetryPolicy(new DefaultRetryPolicy(100000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TAG);
        getRequestQueue().add(req);

    }

    public void cancelPendingResquests(Object tag) {

        if(mResquestQueue != null) {
            mResquestQueue.cancelAll(tag);
        }
    }
}