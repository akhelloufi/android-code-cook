package se.du.jsonparsingvolley;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
f your application makes constant use of the network, it’s probably most efficient to set up a single instance of
RequestQueue that will last the lifetime of your app. You can achieve this in various ways. The recommended approach
is to implement a singleton class that encapsulates RequestQueue and other Volley functionality.

A key concept is that the RequestQueue must be instantiated with the Application context, not an Activity context.
This ensures that the RequestQueue will last for the lifetime of your app, instead of being recreated every time
the activity is recreated (for example, when the user rotates the device).

Remember!
<application android:name=".VolleySingletonHelper">

*/

public class VolleySingletonHelper extends Application {

    public static final String TAG = VolleySingletonHelper.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static VolleySingletonHelper mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }


    /**
     * Singleton main method. Provides the global static instance of the helper class.
     * @return The VolleySingletonHelper instance.
     */
    public static synchronized VolleySingletonHelper getInstance() {
        return mInstance;
    }


    /**
     * Provides the general Volley request queue.
     */
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    /**
     * Adds the request to the general queue.
     * @param req The object Request
     * @param <T> The type of the request result.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        // trying to do a cached request
        if (getRequestQueue().getCache().get(JSONParsingActivity.URL) != null) {
            // response exists
            String cachedResponse = new String(getRequestQueue().getCache().get(JSONParsingActivity.URL).data);
            Log.d(TAG, "Request is cache-hit: " + cachedResponse);
        }
        else {
            getRequestQueue().add(req);
            Log.d(TAG, "Request is fresh: " + req.toString());
        }
    }


    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    /**
     * Cancels all the pending requests.
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }
}