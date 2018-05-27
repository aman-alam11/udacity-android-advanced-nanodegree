package neu.droid.guy.watchify.FirstRunScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.DAO.ListOfMovies;
import neu.droid.guy.watchify.DAO.Movie;
import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.ErrorHandler;
import neu.droid.guy.watchify.NetworkingUtils.NetworkConnectivity;
import neu.droid.guy.watchify.NetworkingUtils.NetworkRequests;
import neu.droid.guy.watchify.RecyclerView.MainActivity;
import neu.droid.guy.watchify.R;

public class FirstRun extends AppCompatActivity {

    /**
     * View Bindings
     */
    @BindView(R.id.splash_screen_parent_layout)
    RelativeLayout mLayoutParentSplashScreen;
    @BindView(R.id.splash_screen_internet_pb)
    ProgressBar mLoadingBar;
    @BindView(R.id.splash_screen_view_pager_container)
    ViewPager mViewPager;

    /**
     * Variables needed for the class and its functions
     */
    private static final String DEFAULT_FIRST_RUN_KEY = "FIRST_RUN";
    private static final String DEFAULT_BUNDLE_KEY = "BUNDLE_KEY";
    private boolean isFirstRun;
    private RequestQueue queue;
    private List<Movie> movieObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make Splash Screen Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_run);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BuildUrl.getContext(this);
        movieObjects = new ArrayList<>();
        //Initialize Network Requests behind Splash Screen
        queue = NetworkRequests.getInstance(FirstRun.this).getRequestQueue();

        // Get the default shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Make a variable with a default value to check for first run
        isFirstRun = preferences.getBoolean(DEFAULT_FIRST_RUN_KEY, true);

        // If its first run, show onBoarding tutorial to educate the user
        if (isFirstRun) {

            //If no internet is there, show the onboarding next time as well
            if (!NetworkConnectivity.isInternetConnectivityAvailable(FirstRun.this)) {
                ErrorHandler.showErrorMessageAndExitApp(FirstRun.this,
                        getResources().getString(R.string.no_internet_connectivity));
                return;
            }

            getDataFromInternet();
            FragmentsAdapter mFragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mFragmentsAdapter);

            //Make the Key Value pair to false
            SharedPreferences.Editor preferenceEditor = preferences.edit().putBoolean(DEFAULT_FIRST_RUN_KEY, false);
            preferenceEditor.apply();
            mLayoutParentSplashScreen.setVisibility(View.GONE);
            mLoadingBar.setVisibility(View.GONE);
            return;
        } else {
            mViewPager.setVisibility(View.GONE);
        }

        // Check if data is already there in Bundle else make a network request
        if (savedInstanceState != null &&
                savedInstanceState.getParcelableArrayList(DEFAULT_BUNDLE_KEY) != null) {

            // Don't make a network request and send old data to main activity
            // as Movie data is not gonna change very frequently.
            Intent intent = new Intent(FirstRun.this, MainActivity.class);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                    (ArrayList<? extends Parcelable>) movieObjects);
            startActivity(intent);
            finish();
        } else {
            // Load data behind splash screen
            getDataFromInternet();
        }
    }

    /**
     * For making Internet calls
     */
    private void getDataFromInternet() {
        // Check for internet
        if (!NetworkConnectivity.isInternetConnectivityAvailable(FirstRun.this)) {
            //Show error message but check Cache for data as well
            if(retrieveCache(BuildUrl.getPopularMovieEndPoint()) != null &&
                    retrieveCache(BuildUrl.getPopularMovieEndPoint()).size() > 0){
                sendIntentWithData(retrieveCache(BuildUrl.getPopularMovieEndPoint()));

                //Don't exit but move forward with cached data
                ErrorHandler.showErrorMessage(FirstRun.this,
                        getResources().getString(R.string.no_internet_connectivity));
            }else {
                // Show Error and Exit
                ErrorHandler.showErrorMessageAndExitApp(FirstRun.this,
                        getResources().getString(R.string.no_internet_connectivity));
            }
        } else {
            // Make network request
            makeNetworkRequest();
        }

    }

    /**
     * Make network request using volley
     */
    private void makeNetworkRequest() {
        String endPoint = BuildUrl.getPopularMovieEndPoint();
        //Check for internet and use cache if not internet
        if (!NetworkConnectivity.isInternetConnectivityAvailable(FirstRun.this)
                && queue.getCache().get(endPoint) != null) {
            // If network is not available, use cache
            List<Movie> cachedMovieObjects = retrieveCache(endPoint);
            sendIntentWithData(cachedMovieObjects);
            return;
        }

        //Make network request
        JsonObjectRequest splashScreenPopularMovieRequest = new JsonObjectRequest(Request.Method.GET,
                BuildUrl.getPopularMovieEndPoint(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ListOfMovies movies = new GsonBuilder()
                                .create()
                                .fromJson(String.valueOf(response),
                                        ListOfMovies.class);
                        movieObjects = movies.getResults();
                        if (!isFirstRun) {
                            sendIntentWithData(movieObjects);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.showErrorMessageAndExitApp(FirstRun.this,
                        getResources().getString(R.string.unknown_error));
            }
        });

        // Start the request
        queue.add(splashScreenPopularMovieRequest);
    }


    /**
    * Deliver Movie Data to Fragments
     * @return List of Movie objects to deliver to Main activity via Intent
     * This method is accessed in Fragments
    * */
    public List<Movie> getMovieData() {
        return movieObjects;
    }


    /**
     * @return Cached list of popular movies
     * */
    private List<Movie> retrieveCache(String endPoint){
        String cachedRes = new String(queue.getCache().get(endPoint).data);
        ListOfMovies mov = new GsonBuilder().create().fromJson(cachedRes, ListOfMovies.class);
        return mov.getResults();
    }

    /**
     * Send the movie data from internet with the intent
     */
    private void sendIntentWithData(List<Movie> movieObjects) {
        // Send Data To Main Activity
        Intent intent = new Intent(FirstRun.this, MainActivity.class);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                (ArrayList<? extends Parcelable>) movieObjects);
        mLoadingBar.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }


    /**
     * Save state in case of configuration change
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movieObjects != null && movieObjects.size() > 0) {
            outState.putParcelableArrayList(DEFAULT_BUNDLE_KEY,
                    (ArrayList<? extends Parcelable>) movieObjects);
        }
        super.onSaveInstanceState(outState);
    }







}