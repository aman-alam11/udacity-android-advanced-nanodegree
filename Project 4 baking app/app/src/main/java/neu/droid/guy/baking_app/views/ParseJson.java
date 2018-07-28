package neu.droid.guy.baking_app.views;
//https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import neu.droid.guy.baking_app.model.Baking;
import neu.droid.guy.baking_app.utils.VolleyNetworkQueue;

public class ParseJson {

    private getJsonResponseAsync mResponseAsync;
    private ErrorListener mDeliverError;
    public static List<Baking> mStaticBakingList;

    public ParseJson(getJsonResponseAsync responseAsync) {
        mResponseAsync = responseAsync;
    }

    /**
     * @param urlToHit
     * @param context
     */
    public void makeNetworkRequest(String urlToHit, Context context, final ErrorListener errorListener) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlToHit,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJsonArrayUsingGson(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(this.getClass().getSimpleName(), error.toString());
                        mDeliverError = errorListener;
                        mDeliverError.getErrorMessage(error.getMessage());
                    }
                });

        VolleyNetworkQueue.getInstance(context).getRequestQueue().add(arrayRequest);
    }

    /**
     * Parse the JSON Array using GSON
     *
     * @param response
     */
    public void parseJsonArrayUsingGson(String response) {
        // Use Gson to parse response
        Gson gson = new Gson();
        Type bakingResponseListType = new TypeToken<ArrayList<Baking>>() {
        }.getType();
        // Change the JsonArray to ArrayList of Baking objects
        List<Baking> listOfBakingObjects = gson.fromJson(response, bakingResponseListType);
        if (mStaticBakingList == null) {
            mStaticBakingList = new ArrayList<>();
        }
        mStaticBakingList.addAll(listOfBakingObjects);
        // To send data to calling activity async
        mResponseAsync.getResponse(listOfBakingObjects);
    }


    /**
     * An interface to transfer data to calling activity in an async manner
     */
    public interface getJsonResponseAsync {
        void getResponse(List<Baking> listOfBaking);
    }
}
