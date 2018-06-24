package neu.droid.guy.watchify.MainActivity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.Lambdas.checkMovieDataValidity;
import neu.droid.guy.watchify.POJO.ListOfMovies;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.DetailsActivity.DetailsMovie;
import neu.droid.guy.watchify.FavActivity.ShowFavMovies;
import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.VolleyRequestQueueSingleton;
import neu.droid.guy.watchify.R;

/**
 * REFERENCES:
 * PLACEHOLDER IMAGE: https://openclipart.org/detail/211804/movie-scene-marker-matticonsplayer3
 * ERROR IMAGE : https://openclipart.org/detail/301359/webpage-not-available
 */


/**
 * OPTIONAL TODO
 * Get Thumbnails from Youtube API
 * First Run screens common buttons use <include> tag : Use Include in XML wherever needed
 * Key in manifest
 * <p>
 * CHANGE NOTIFICATIONS bar when changing form top rated to popular movies
 */


public class MainActivity extends AppCompatActivity
        implements VolleyRequestQueueSingleton.JSONRecievedCallback {

    /**
     * Bind Views
     */
    @BindView(R.id.movie_recycler_view)
    RecyclerView moviesRecyclerView;
    @BindView(R.id.main_pb)
    ProgressBar mLoadingBar;

    /**
     * Init
     */
    List<Movie> mMoviesData;
    boolean isPopularMovieDataVisible = true;
    String savedInstanceDataKey = "POPULAR_MOVIES_VISIBLE";
    public static final String detailsIntentDataKey = "DETAILS_EXTRA";
    public static final String DATA_TYPE_MOVIE = "MOVIE";
    private static final int NUM_COLUMNS = 2;
    WatchifyAdapter mWatchifyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(getResources().getString(R.string.popular_movies_activity_title));
        BuildUrl.setContext(this);

        /** Restore state in case of configuration changes */
        if (savedInstanceState != null) {
            showDataLoadingView(true);
            isPopularMovieDataVisible = savedInstanceState.getBoolean(savedInstanceDataKey);
            if (isPopularMovieDataVisible) {
                restoreDataFromBundle(savedInstanceState, getResources().getString(R.string.popular_movies_activity_title));
            } else {
                restoreDataFromBundle(savedInstanceState, getResources().getString(R.string.top_movies_activity_title));
            }
        }

        if (savedInstanceState == null &&
                getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM) != null) {
            showDataLoadingView(true);
            /** Load Data From Intent */
            mMoviesData = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            moviesRecyclerView.setVisibility(View.VISIBLE);
        } else if (savedInstanceState == null &&
                getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM) != null) {
            /** Make Internet Request again */
            checkMovieDataValidity movieDataValidity = (data) -> (data == null || data.size() <= 0);
            getDataFromCacheIfAvailable(movieDataValidity.isDataInValid(mMoviesData),
                    BuildUrl.getPopularMovieEndPoint());
        }

        initRecyclerView();
    }

    /**
     * Initialize the Recycler View
     */
    private void initRecyclerView() {

        GridLayoutManager gridManager =
                new GridLayoutManager(MainActivity.this,
                        NUM_COLUMNS,
                        GridLayoutManager.VERTICAL,
                        false);
        moviesRecyclerView.setLayoutManager(gridManager);
        moviesRecyclerView.setHasFixedSize(false);
        mWatchifyAdapter = new WatchifyAdapter(mMoviesData, index -> {
            Intent openDetailsActivity = new Intent(MainActivity.this, DetailsMovie.class);
            openDetailsActivity.putExtra(detailsIntentDataKey, mMoviesData.get(index));
            startActivity(openDetailsActivity);
        });
        moviesRecyclerView.setAdapter(mWatchifyAdapter);
        showDataLoadingView(false);
    }


    /**
     * Get Data From Bundle in case of screen rotation
     *
     * @param savedInstanceState the saved state that is to be restored on rotation
     * @param activityTitle      the activity title to set in case of rotation
     */
    private void restoreDataFromBundle(Bundle savedInstanceState,
                                       String activityTitle) {
        setTitle(activityTitle);
        mMoviesData = savedInstanceState.getParcelableArrayList(Intent.EXTRA_STREAM);
    }

    /**
     * Inflate the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }


    /**
     * Handle click on the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        checkMovieDataValidity movieDataValidity = (data) -> (data == null || data.size() <= 0);
        switch (item.getItemId()) {

            case R.id.menu_option_sort_ratings:
                isPopularMovieDataVisible = false;
                getDataFromCacheIfAvailable(movieDataValidity.isDataInValid(mMoviesData),
                        BuildUrl.getTopRatedMovieEndPoint());
                setTitle(getResources().getString(R.string.top_movies_activity_title));
                break;

            case R.id.menu_option_sort_popular:
                isPopularMovieDataVisible = true;
                getDataFromCacheIfAvailable(movieDataValidity.isDataInValid(mMoviesData),
                        BuildUrl.getPopularMovieEndPoint());
                setTitle(getResources().getString(R.string.popular_movies_activity_title));
                break;

            case R.id.menu_option_favourites:
                Intent openFavMovies = new Intent(MainActivity.this, ShowFavMovies.class);
                startActivity(openFavMovies);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks for Cached data using Volley.
     * If Cached data is not available, makes a new API request
     *
     * @param isDataInvalid Checks if the Cache is valid or not. If Cache data is invalid,
     *                      then it makes a new internet request to get fresh data
     * @param endpoint      The URL for which we need the data
     */
    private void getDataFromCacheIfAvailable(Boolean isDataInvalid, String endpoint) {
        showDataLoadingView(true);
        if (isDataInvalid) {
            makeNetworkRequest(endpoint);
        } else {
            /** Retrieve Data from Cache */
            retrieveDataFromCache(endpoint);
        }
    }

    /**
     * Retrieve data from cache
     */
    private void retrieveDataFromCache(String endPointKey) {
        RequestQueue queue = VolleyRequestQueueSingleton.getInstance(this).getRequestQueue();
        Cache.Entry cachedMoviesEntry = queue.getCache().get(endPointKey);
        if (cachedMoviesEntry != null) {
            String cachedRes = new String(queue.getCache().get(endPointKey).data);
            ListOfMovies mov = new GsonBuilder().create().fromJson(cachedRes, ListOfMovies.class);
            mMoviesData = mov.getResults();
            initRecyclerView();
            showDataLoadingView(false);
        } else {
            makeNetworkRequest(endPointKey);
        }
    }


    /**
     * Make a network request to get updated data
     */
    private void makeNetworkRequest(String endPoint) {
        VolleyRequestQueueSingleton.getInstance(this).updateCallback(this);
        VolleyRequestQueueSingleton.getInstance(this).commonNetworkRequest(endPoint, DATA_TYPE_MOVIE);
    }

    /**
     * @param responseAsString The string which is retrieved from JSON
     */
    @Override
    public void jsonRecieved(String responseAsString, String dataType) {
        ListOfMovies movies = new GsonBuilder().create().fromJson(String.valueOf(responseAsString),
                ListOfMovies.class);
        mMoviesData.clear();
        mMoviesData = movies.getResults();
        runOnUiThread(() -> {
            showDataLoadingView(false);
            initRecyclerView();
        });

    }

    /**
     * Save state in case of configuration changes
     * If not done, top rated movie would change to popular movie
     * as popular movie is default one which is loaded on splash screen.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isPopularMovieDataVisible) {
            outState.putBoolean(savedInstanceDataKey, isPopularMovieDataVisible);
            outState.putParcelableArrayList(Intent.EXTRA_STREAM,
                    (ArrayList<? extends Parcelable>) mMoviesData);
        } else {
            outState.putBoolean(savedInstanceDataKey, isPopularMovieDataVisible);
            outState.putParcelableArrayList(Intent.EXTRA_STREAM,
                    (ArrayList<? extends Parcelable>) mMoviesData);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * @param isLoading Show/Hide views based on Boolean isLoading variable
     */
    private void showDataLoadingView(Boolean isLoading) {
        if (isLoading) {
            mLoadingBar.setVisibility(View.VISIBLE);
            moviesRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mLoadingBar.setVisibility(View.INVISIBLE);
            moviesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}