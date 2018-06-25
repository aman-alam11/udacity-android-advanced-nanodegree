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

import com.android.volley.RequestQueue;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.POJO.ListOfMovies;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.ErrorHandler;
import neu.droid.guy.watchify.NetworkingUtils.NetworkConnectivityChecker;
import neu.droid.guy.watchify.NetworkingUtils.VolleyRequestQueueSingleton;
import neu.droid.guy.watchify.MainActivity.MainActivity;
import neu.droid.guy.watchify.R;


public class FirstRun extends AppCompatActivity implements VolleyRequestQueueSingleton.JSONRecievedCallback {

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
    private RequestQueue queue;
    private List<Movie> movieObjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Make Splash Screen Full Screen */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**Initialization*/
        setContentView(R.layout.activity_first_run);
        ButterKnife.bind(this);
        queue = VolleyRequestQueueSingleton.getInstance(FirstRun.this).getRequestQueue();
        movieObjects = new ArrayList<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        /** If its first run, show onBoarding tutorial to educate the user */
        if (isFirstRun()) {

            /** If no internet is there, show the onboarding next time as well */
            if (!isInternetAvailable()) {
                ErrorHandler.showErrorMessageAndExitApp(FirstRun.this, getResources().getString(R.string.no_internet_connectivity));
                return;
            } else {
                makeNetworkRequest(BuildUrl.getPopularMovieEndPoint());
                showFirstRunScreens();
            }
            return;
        } else {
            useCachedDataIfAvailable();
        }

    }

    /**
     * If there had been previous calls, there might be cached data available
     * Use cached data wherever possible as movie data won't change very frequently
     */
    private void useCachedDataIfAvailable() {
        if (retrieveCache(BuildUrl.getPopularMovieEndPoint()) != null &&
                retrieveCache(BuildUrl.getPopularMovieEndPoint()).size() > 0) {
            /** Move to Main Activity */
            sendIntentWithData(retrieveCache(BuildUrl.getPopularMovieEndPoint()));
        } else {
            if (isInternetAvailable()) {
                makeNetworkRequest(BuildUrl.getPopularMovieEndPoint());
            } else {
                ErrorHandler.showErrorMessageAndExitApp(this, "No Internet Available");
            }
        }
    }

    /**
     * Unhide the View Pager and hide the splash screen
     * Show the First Run Fragments
     */
    private void showFirstRunScreens() {
        mViewPager.setVisibility(View.VISIBLE);
        FragmentsAdapter mFragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentsAdapter);

        mLayoutParentSplashScreen.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.GONE);
    }

    /**
     * Once user has onboarded/educated about the app, update the Shared Preferences
     */
    private void updateSharedPreferences() {
        //Make the Key Value pair to false
        SharedPreferences.Editor preferenceEditor = PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(DEFAULT_FIRST_RUN_KEY, false);
        preferenceEditor.apply();
    }

    /**
     * Make network request using volley
     * The updateCallback() method gets the JSON asynchronously,
     * when the background Volley thread returns it
     * <p>
     * The common network request is a method that gets the only instantiated network queue
     * This network queue is implemented as Singleton pattern so that there is only 1 instance
     * Get the queue and add our request to it
     */
    private void makeNetworkRequest(String urlToHit) {
        VolleyRequestQueueSingleton.getInstance(this).updateCallback(this);
        VolleyRequestQueueSingleton.getInstance(this).commonNetworkRequest(urlToHit, "MOVIE");
    }

    /**
     * Deliver Movie Data to Fragments
     *
     * @return List of Movie objects to deliver to Main activity via Intent
     * This method is accessed in Fragments
     */
    public List<Movie> getMovieData() {
        return movieObjects;
    }


    /**
     * @param endPoint is the url which is used as a key to get the cached data
     * @return Cached list of popular movies
     */
    private List<Movie> retrieveCache(String endPoint) {
        try {
            if (queue.getCache().get(endPoint).data == null) {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        String cachedRes = new String(queue.getCache().get(endPoint).data);
        ListOfMovies mov = new GsonBuilder().create().fromJson(cachedRes, ListOfMovies.class);
        return mov.getResults();
    }

    /**
     * Send the movie data from internet with the intent
     *
     * @param movieObjects The movieObject has all the list of movies which will be displayed in Recycler View in MainActivity()
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
     * Asynchronously gets called when data is received from API
     * Update the UI in case it is not the first run
     * If it is the first run, UI is updated on button click (Skip Button / Done Button)
     *
     * @param responseAsString : The string which has all the API response data
     */
    @Override
    public void jsonRecieved(String responseAsString, String dataType) {
        ListOfMovies movies = new GsonBuilder().create().fromJson(String.valueOf(responseAsString), ListOfMovies.class);
        movieObjects = movies.getResults();
        if (!isFirstRun()) {
            // Update the UI
            runOnUiThread(() -> sendIntentWithData(movieObjects));
        } else {
            updateSharedPreferences();
        }
    }


    /**
     * Checks if Internet connectivity is available or not
     */
    private Boolean isInternetAvailable() {
        return NetworkConnectivityChecker.isInternetConnectivityAvailable(FirstRun.this);
    }

    /**
     * @return a Boolean value from Shared preferences which tells if this run is 1st run or not
     */
    private Boolean isFirstRun() {
        /** Get the default shared preferences */
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(DEFAULT_FIRST_RUN_KEY, true);
    }

}