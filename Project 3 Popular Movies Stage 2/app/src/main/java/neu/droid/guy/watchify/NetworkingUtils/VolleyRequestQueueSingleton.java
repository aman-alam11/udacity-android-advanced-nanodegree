package neu.droid.guy.watchify.NetworkingUtils;

import android.content.Context;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * The singleton class for network requests using volley
 */
public class VolleyRequestQueueSingleton {

    private RequestQueue mRequestQueue;
    private static VolleyRequestQueueSingleton mNetworkRequestInstance;
    private static JSONRecievedCallback jsonRecievedCallback;
    private int INITIAL_TIMEOUT_MILLISECONDS = 3000;
    private String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Default constructor
     *
     * @param context
     */
    private VolleyRequestQueueSingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * If there is no request queue available, we update mRequestQueue with a new Request Queue
     * Else we return the same instance of already available queue
     */
    public static VolleyRequestQueueSingleton getInstance(Context context) {
        if (mNetworkRequestInstance == null) {
            mNetworkRequestInstance = new VolleyRequestQueueSingleton(context);
        }
        return mNetworkRequestInstance;
    }

    /**
     * Returns the instance of request queue
     */
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    /**
     * Where the actual request is made to API
     * This runs on background thread which is maintained by volley
     * As soon as we get the results in an async manner, we update the activity via callback
     * TODO: Add Error Handler message in FunctionalInterface for Lambda function
     *
     * @param urlToHit
     */
    public void commonNetworkRequest(String urlToHit, String dataType) {
        Log.e(LOG_TAG, urlToHit);
        NetworkRequest networkRequest = (url) -> {
            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            /**Send the results to Calling Activity via callback*/
                            jsonRecievedCallback.jsonRecieved(String.valueOf(response), dataType);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error != null && error.networkResponse != null) {
                                Log.e("Error in response", String.valueOf(error.networkResponse.statusCode));
                            }
                        }
                    }
            );


            objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    INITIAL_TIMEOUT_MILLISECONDS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return objectRequest;
        };

        /** Start the request */
        mNetworkRequestInstance
                .getRequestQueue()
                .add(networkRequest.makeNetworkRequest(urlToHit));
    }

    /**
     * Here we update the Interface callback so that jsonRecievedCallback is not null
     */
    public void updateCallback(JSONRecievedCallback callback) {
        jsonRecievedCallback = callback;
    }


    /**
     * An interface for callback for transferring the JSON response to calling activity
     */
    public interface JSONRecievedCallback {
        void jsonRecieved(String responseAsString, String DataTypeRecieved);
    }


}
