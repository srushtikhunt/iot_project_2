package com.example.powerautomation;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class VolleySingleton extends Application {
    public static final String TAG = VolleySingleton.class.getSimpleName();
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static VolleySingleton mSingleton;

    public VolleySingleton(Context context) {
        this.mContext = context;
        this.mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

    }

    public static synchronized VolleySingleton getInstance(Context context){
        if(mSingleton == null){
            mSingleton = new VolleySingleton(context);
        }
        return mSingleton;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        //set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader(){
        return this.mImageLoader;
    }
}

