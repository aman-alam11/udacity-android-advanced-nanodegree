package neu.droid.guy.watchify.RecyclerView;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.DAO.ListOfMovies;
import neu.droid.guy.watchify.DAO.Movie;
import neu.droid.guy.watchify.FirstRunScreens.FirstRun;
import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.ErrorHandler;
import neu.droid.guy.watchify.NetworkingUtils.NetworkRequests;
import neu.droid.guy.watchify.R;

// TODO Stage 2: Pull down to refresh functionality
// TODO Stage 2: M/W animation first run screens
// TODO: Key in manifest
// REFERENCES:
// PLACEHOLDER IMAGE: https://openclipart.org/detail/211804/movie-scene-marker-matticonsplayer3
// ERROR IMAGE : https://openclipart.org/detail/301359/webpage-not-available


public class MainActivity extends AppCompatActivity {
//        implements SwipeRefreshLayout.OnRefreshListener {

    //Bind Views
    @BindView(R.id.movie_recycler_view)
    RecyclerView moviesRecyclerView;
    @BindView(R.id.main_pb)
    ProgressBar mLoadingBar;
//    @BindView(R.id.swipe_layout_container)
//    SwipeRefreshLayout mSwipeRefresh;


    List<Movie> mPopularMoviesList;
    List<Movie> mTopRatedMoviesList;
    RequestQueue queue;
    boolean isPopularMovieDataVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(getResources().getString(R.string.popular_movies_activity_title));
        setTitleColor(getResources().getColor(R.color.colorBrightGreen));
        BuildUrl.getContext(this);

        //Restore state in case of configuration changes
        if (savedInstanceState != null) {
            //Restore from savedState Bundle
            if (savedInstanceState.getBoolean("POPULAR_MOVIES_VISIBLE")) {
                mPopularMoviesList = savedInstanceState.getParcelableArrayList(Intent.EXTRA_STREAM);
                setTitle(getResources().getString(R.string.popular_movies_activity_title));
                isPopularMovieDataVisible = true;
            } else {
                mTopRatedMoviesList = savedInstanceState.getParcelableArrayList(Intent.EXTRA_STREAM);
                isPopularMovieDataVisible = false;
                setTitle(getResources().getString(R.string.top_movies_activity_title));
            }
        } else {
            mPopularMoviesList = new ArrayList<>();
            mTopRatedMoviesList = new ArrayList<>();
        }


        queue = NetworkRequests.getInstance(MainActivity.this).getRequestQueue();
        if (getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM) != null) {
            mPopularMoviesList = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            mLoadingBar.setVisibility(View.INVISIBLE);
            moviesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            //Make Internet Request again
            mLoadingBar.setVisibility(View.VISIBLE);
            makeNetworkRequest(BuildUrl.getPopularMovieEndPoint(), true);
        }

        GridLayoutManager gridManager =
                new GridLayoutManager(MainActivity.this,
                        2,
                        GridLayoutManager.VERTICAL,
                        false);
        moviesRecyclerView.setLayoutManager(gridManager);
        moviesRecyclerView.setHasFixedSize(false);

        // Handle Clicks on recycler view items
        if (savedInstanceState != null) {
            updateRecyclerView(savedInstanceState.<Movie>getParcelableArrayList(Intent.EXTRA_STREAM));
        } else {
            updateRecyclerView(mPopularMoviesList);
        }
    }


    /**
     * Handle click on the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_option_sort_ratings:
                mLoadingBar.setVisibility(View.VISIBLE);
                isPopularMovieDataVisible = false;
                if (mTopRatedMoviesList == null || mTopRatedMoviesList.size() <= 0) {
                    makeNetworkRequest(BuildUrl.getTopRatedMovieEndPoint(),
                            isPopularMovieDataVisible);
                } else {
                    //Retrieve Data from Cache
                    retrieveDataFromCache(BuildUrl.getTopRatedMovieEndPoint(),
                            isPopularMovieDataVisible);
                }
                setTitle(getResources().getString(R.string.top_movies_activity_title));
                break;

            case R.id.menu_option_sort_popular:
                mLoadingBar.setVisibility(View.VISIBLE);
                isPopularMovieDataVisible = true;
                if (mPopularMoviesList == null || mPopularMoviesList.size() <= 0) {
                    makeNetworkRequest(BuildUrl.getPopularMovieEndPoint(),
                            isPopularMovieDataVisible);
                } else {
                    //Retrieve Data from Cache
                    retrieveDataFromCache(BuildUrl.getPopularMovieEndPoint(),
                            isPopularMovieDataVisible);
                }
                setTitle(getResources().getString(R.string.popular_movies_activity_title));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Retrieve data from cache
     */
    private void retrieveDataFromCache(String endPointKey, Boolean isPopularMovieData) {
        mLoadingBar.setVisibility(View.VISIBLE);
        Cache.Entry cachedMoviesEntry = queue.getCache().get(endPointKey);
        if (cachedMoviesEntry != null) {
            String cachedRes = new String(queue.getCache().get(endPointKey).data);
            ListOfMovies mov = new GsonBuilder().create().fromJson(cachedRes, ListOfMovies.class);
            if (isPopularMovieData) {
                mPopularMoviesList = mov.getResults();
                updateRecyclerView(mPopularMoviesList);
            } else {
                mTopRatedMoviesList = mov.getResults();
                updateRecyclerView(mTopRatedMoviesList);
            }
            mLoadingBar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Make a network request to get updated data
     */
    private void makeNetworkRequest(String endPoint, final Boolean isPopularMovieData) {

        GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        JsonObjectRequest movieRequest = new JsonObjectRequest(Request.Method.GET,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ListOfMovies movies = gson.fromJson(String.valueOf(response),
                                ListOfMovies.class);
                        if (isPopularMovieData) {
                            mPopularMoviesList = movies.getResults();
                            updateRecyclerView(mPopularMoviesList);
                        } else {
                            mTopRatedMoviesList = movies.getResults();
                            updateRecyclerView(mTopRatedMoviesList);
                        }
                        mLoadingBar.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.showErrorMessage(MainActivity.this, error.getMessage());
            }
        });
        queue.add(movieRequest);
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
     * Set the adapter with new data and handle clicks
     * Setting to new adapter instead of swapAdapter as the image
     * in the recycler views were not getting updated even though the data was getting updated.
     * RV was still showing data from cache
     */
    private void updateRecyclerView(final List<Movie> movieData) {
        moviesRecyclerView.setAdapter(new WatchifyAdapter(movieData, new WatchifyAdapter.clickListener() {
            @Override
            public void itemClicked(int index) {
                Intent openDetailsActivity = new Intent(MainActivity.this, DetailsMovie.class);
                openDetailsActivity.putExtra("DETAILS_EXTRA", movieData.get(index));
                startActivity(openDetailsActivity);
            }
        }));
    }


    /**
     * Save state in case of configuration changes
     * If not done, top rated movie would change to popular movie
     * as popular movie is default one which is loaded on splash screen.
     * */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isPopularMovieDataVisible) {
            outState.putBoolean("POPULAR_MOVIES_VISIBLE", true);
            outState.putParcelableArrayList(Intent.EXTRA_STREAM,
                    (ArrayList<? extends Parcelable>) mPopularMoviesList);
        } else {
            outState.putBoolean("POPULAR_MOVIES_VISIBLE", false);
            outState.putParcelableArrayList(Intent.EXTRA_STREAM,
                    (ArrayList<? extends Parcelable>) mTopRatedMoviesList);
        }
        super.onSaveInstanceState(outState);
    }


//    @Override
//    public void onRefresh() {
//        Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_LONG).show();
//        mSwipeRefresh.setRefreshing(false);
//    }

}