package neu.droid.guy.watchify.NetworkingUtils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import neu.droid.guy.watchify.DAO.ListOfMovies;
import neu.droid.guy.watchify.DAO.Movie;
import neu.droid.guy.watchify.FirstRunScreens.FirstRun;
import neu.droid.guy.watchify.R;

/*The singleton class for network requests using volley*/
public class NetworkRequests {

    private RequestQueue mRequestQueue;
    private static NetworkRequests mNetworkRequestInstance;

    /**
     * Default constructor
     */
    private NetworkRequests(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * If there is no request queue available, we update mRequestQueue with a new Request Queue
     * Else we return the same instance of already available queue
     */
    public static NetworkRequests getInstance(Context context) {
        if (mNetworkRequestInstance == null) {
            mNetworkRequestInstance = new NetworkRequests(context);
        }
        return mNetworkRequestInstance;
    }

    /**
     * Returns the instance of request queue
     * */
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}
