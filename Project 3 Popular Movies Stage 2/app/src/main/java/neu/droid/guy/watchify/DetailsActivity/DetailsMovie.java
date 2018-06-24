package neu.droid.guy.watchify.DetailsActivity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.Lambdas.GetKeysLambda;
import neu.droid.guy.watchify.NetworkingUtils.NetworkConnectivityChecker;
import neu.droid.guy.watchify.POJO.FavMovies;
import neu.droid.guy.watchify.POJO.ListOfReviews;
import neu.droid.guy.watchify.POJO.ListOfVideos;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.POJO.Review;
import neu.droid.guy.watchify.POJO.Video;
import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.VolleyRequestQueueSingleton;
import neu.droid.guy.watchify.R;
import neu.droid.guy.watchify.Room.AppDatabase;
import neu.droid.guy.watchify.ViewModel.AddMovieViewModel;
import neu.droid.guy.watchify.ViewModel.ThreadExecutors;
import neu.droid.guy.watchify.ViewModel.ViewModelFactory;

import static neu.droid.guy.watchify.MainActivity.MainActivity.detailsIntentDataKey;

public class DetailsMovie extends AppCompatActivity
        implements VolleyRequestQueueSingleton.JSONRecievedCallback,
        ReviewAdapter.clickListenerReviewTextView, VideoAdapter.videoRecyclerViewClickListener {

    @BindView(R.id.details_backdrop_image_view)
    ImageView backdropImageView;
    @BindView(R.id.details_text_view)
    TextView descriptionMovieTextView;
    @BindView(R.id.details_movie_title)
    TextView mMovieTitleTextView;
    @BindView(R.id.details_vote_average)
    TextView mAverageVoteTextView;
    @BindView(R.id.details_language)
    TextView mLanguageTextView;
    @BindView(R.id.release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.fab_fav)
    FloatingActionButton favFab;
    @BindView(R.id.ratings_bar_details_view)
    RatingBar mRatingsBar;
    @BindView(R.id.review_title_text_view)
    TextView mReviewTitleTextView;

    /**
     * Parent Card Views
     */
    @BindView(R.id.review_parent_layout)
    CardView reviewParentView;

    /**
     * Recycler Views and progress bars, adapters
     */
    @BindView(R.id.reviews_recycler_view)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.video_recycler_view)
    RecyclerView videoRecyclerView;

    /**
     * Params Init
     */
    private String movieId;
    private AppDatabase mDB;
    private Boolean isMovieFav;
    private Boolean isMaterialDialogOpen;
    private static final String MATERIAL_DIALOG_REVIEW_KEY = "MATERIAL_DIALOG_REVIEW_KEY";
    private static final String DATA_TYPE_VIDEO = "VIDEO";
    private static final String DATA_TYPE_REVIEW = "REVIEW";
    private final int NUM_SPANS = 1;
    private String reviewOfItem;
    private List<Video> mListOfVideoObjects;
    private List<Review> mListOfReviewObjects;
    private MaterialDialog reviewDialog;
    Movie mMovieObjectData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
        ButterKnife.bind(this);
        BuildUrl.setContext(this);
        changeNotificationPaneColor();
        favFab.setVisibility(View.INVISIBLE);

        /** Get Data From Intent */
        if (getIntent().getExtras() != null) {
            mMovieObjectData = getIntent().getExtras().getParcelable(detailsIntentDataKey);
        } else {
            return;
        }

        /**
         * Show Dialog box if someone minimized the app while dialog box is open
         */
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(MATERIAL_DIALOG_REVIEW_KEY) != null) {
                reviewOfItem = savedInstanceState.getString(MATERIAL_DIALOG_REVIEW_KEY);
                if (reviewDialog == null)
                    openMaterialDialog();
            }
        }

        assert mMovieObjectData != null;
        /** Make a call to internet to get Movie Trailers using this id */
        movieId = mMovieObjectData.getMovieId();
        /** Init for Fav Movie Database */
        mDB = AppDatabase.getInstance(getApplicationContext());

        /** Load Backdrop Images */
        if (mMovieObjectData.getBackdropImageURL() != null) {
            /** Set placeholder and error images accordingly */
            Picasso.get()
                    .load(mMovieObjectData.getBackdropImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.image_error)
                    .into(backdropImageView);
        }

        /** Get all data from Intent and set it for multiple TextViews */
        setupDetailsMovie(mMovieObjectData);

        /** Initialize the Recycler View and Adapter for various views */
        setupRecyclerViewReviews();

        /** Check if movie is in Fav */
        checkMovieForFav();

        /** The Add/Remove from Favourites Button */
        favFab.setOnClickListener(v -> toggleMovieDataDatabase());

        /** Check Cache for Data */
        if (mListOfReviewObjects == null) {
            retrieveDataFromCache(BuildUrl.getReviewsForMovie(movieId), DATA_TYPE_REVIEW);
        }
        if (mListOfVideoObjects == null) {
            retrieveDataFromCache(BuildUrl.getVideoDetails(movieId), DATA_TYPE_VIDEO);
        }
    }


    /**
     * Initialize Recycler View
     */
    private void setupRecyclerViewReviews() {
        GridLayoutManager gridManager =
                new GridLayoutManager(DetailsMovie.this,
                        1,
                        GridLayoutManager.VERTICAL,
                        false);
        GridLayoutManager gridManagerVideo =
                new GridLayoutManager(DetailsMovie.this,
                        NUM_SPANS,
                        GridLayoutManager.HORIZONTAL,
                        false);

        reviewsRecyclerView.setLayoutManager(gridManager);
        reviewsRecyclerView.setHasFixedSize(false);

        videoRecyclerView.setLayoutManager(gridManagerVideo);
        videoRecyclerView.setHasFixedSize(false);
    }


    /**
     * Check if the data is available in cache. If it is available, use it.
     * Otherwise fetch fresh data from internet
     *
     * @param endPointKey     The end point that includes the url along with the movie id
     * @param dataTypeToFetch The data type to fetch which be either VIDEO or REVIEW
     */
    private void retrieveDataFromCache(String endPointKey, String dataTypeToFetch) {
        RequestQueue queue = VolleyRequestQueueSingleton.getInstance(this).getRequestQueue();
        Cache.Entry cachedMoviesEntry = queue.getCache().get(endPointKey);
        if (cachedMoviesEntry != null) {
            String cachedRes = new String(queue.getCache().get(endPointKey).data);
            if (dataTypeToFetch.equals(DATA_TYPE_VIDEO)) {
                ListOfVideos videosClass = new GsonBuilder().create().fromJson(cachedRes, ListOfVideos.class);
                mListOfVideoObjects = videosClass.getResults();
                showRecyclerView(DATA_TYPE_VIDEO);
            } else if (dataTypeToFetch.equals(DATA_TYPE_REVIEW)) {
                ListOfReviews reviewsClass = new GsonBuilder().create().fromJson(cachedRes, ListOfReviews.class);
                mListOfReviewObjects = reviewsClass.getResults();
                showRecyclerView(DATA_TYPE_REVIEW);
            }
        } else {
            makeNetworkRequest(endPointKey, dataTypeToFetch);
        }
    }


    /**
     * If data is not available, this is where the internet call is initiated
     * This is the starting point to make the call from this activity.
     * This function calls the volley singleton class
     *
     * @param movieURL        The url with the movie id that will be included with the url
     * @param dataTypeToFetch The data type to fetch which be either VIDEO or REVIEW
     */
    private void makeNetworkRequest(String movieURL, String dataTypeToFetch) {
        if (!NetworkConnectivityChecker.isInternetConnectivityAvailable(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connectivity),
                    Toast.LENGTH_SHORT).show();
        }
        VolleyRequestQueueSingleton.getInstance(this).updateCallback(this);
        if (dataTypeToFetch.equals(DATA_TYPE_VIDEO)) {
            VolleyRequestQueueSingleton
                    .getInstance(this)
                    .commonNetworkRequest(movieURL, dataTypeToFetch);
        } else if (dataTypeToFetch.equals(DATA_TYPE_REVIEW)) {
            VolleyRequestQueueSingleton
                    .getInstance(this)
                    .commonNetworkRequest(movieURL, dataTypeToFetch);
        }
    }


    /**
     * This is the implementation of the interface that is basically the
     * callback for transferring the JSON response to calling activity
     *
     * @param responseAsString The response received from API as String
     * @param dataTypeReceived The response for VIDEO or REVIEW
     */
    @Override
    public void jsonRecieved(String responseAsString, String dataTypeReceived) {
        if (dataTypeReceived.equals(DATA_TYPE_VIDEO)) {

            /** Parse Youtube URLs from JSON */
            ListOfVideos videoObjects =
                    new GsonBuilder().create().fromJson(String.valueOf(responseAsString), ListOfVideos.class);
            mListOfVideoObjects = videoObjects.getResults();
            showRecyclerView(DATA_TYPE_VIDEO);
        } else if (dataTypeReceived.equals(DATA_TYPE_REVIEW)) {

            ListOfReviews reviewsList = new GsonBuilder().create().fromJson(String.valueOf(responseAsString),
                    ListOfReviews.class);
            mListOfReviewObjects = reviewsList.getResults();
            showRecyclerView(DATA_TYPE_REVIEW);
        }
    }


    /**
     * Show the video using Recycler Views
     *
     * @param type The type of data to display
     */
    void showRecyclerView(String type) {
        if (type.equals(DATA_TYPE_VIDEO)) {
            GetKeysLambda keysLambda = (listOfVidObj) -> {
                List<String> listOfKeys = new ArrayList<>(listOfVidObj.size());
                for (int i = 0; i < listOfVidObj.size(); i++) {
                    listOfKeys.add(listOfVidObj.get(i).getKey());
                }
                return listOfKeys;
            };
            List<String> videoURLs = keysLambda.getAllKeys(mListOfVideoObjects);
            if (mListOfVideoObjects.size() > 0) {
                VideoAdapter mVideoAdapter = new VideoAdapter(mListOfVideoObjects, videoURLs, this);
                videoRecyclerView.setAdapter(mVideoAdapter);
            } else {
                // Since there are no videos to show
                Toast.makeText(this, getResources().getString(R.string.no_videos_available),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (type.equals(DATA_TYPE_REVIEW)) {
            if (mListOfReviewObjects.size() > 0) {
                ReviewAdapter mReviewAdapter = new ReviewAdapter(mListOfReviewObjects, this);
                reviewsRecyclerView.setAdapter(mReviewAdapter);
                mReviewTitleTextView.setVisibility(View.VISIBLE);
            } else {
                // Since there are no videos to show
                Toast.makeText(this, getResources().getString(R.string.no_reviews_available),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Show full review on item click
     *
     * @param index  Index of review among review recycler view
     * @param review The actual review text which was clicked upon
     */
    @Override
    public void itemClicked(int index, String review, String authorName) {
        reviewOfItem = review;
        openMaterialDialog();
    }


    /**
     * Show the material dialog and display the review in the dialog box
     */
    private void openMaterialDialog() {
        isMaterialDialogOpen = true;
        if (reviewOfItem == null || TextUtils.isEmpty(reviewOfItem)) {
            return;
        }
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.app_name))
                .content(reviewOfItem)
                .positiveText(getResources().getString(R.string.positive_button_text))
                .backgroundColor(getResources().getColor(R.color.colorAccentGRAY))
                .cancelable(false)
                .onPositive((dialog, which) -> {
                    // To Avoid Window Leak: E/WindowManager: android.view.WindowLeaked:
                    isMaterialDialogOpen = false;
                    reviewDialog = null;
                });
        reviewDialog = dialogBuilder.build();
        reviewDialog.show();
    }

    /**
     * Setup all details from intent
     *
     * @param mMovieObjectData Populate All the views using the data sent from the previous screen
     */
    private void setupDetailsMovie(Movie mMovieObjectData) {
        descriptionMovieTextView.setText(mMovieObjectData.getMovieDescription());
        mMovieTitleTextView.setText(mMovieObjectData.getMovieName());
        mAverageVoteTextView.setText(mMovieObjectData.getAverageVote());
        mLanguageTextView.setText(mMovieObjectData.getMovieLanguage().toUpperCase());
        mReleaseDateTextView.setText(getResources().getString(R.string.release_date_tag));
        mReleaseDateTextView.append(mMovieObjectData.getReleaseDate());

        mRatingsBar.setRating(Float.valueOf(mMovieObjectData.getAverageVote()) / 2);
    }


    /**
     * For Sending Intent to Youtube
     * <p>
     * https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
     */
    @Override
    public void reportItemClicked(String url) {
        Intent openYoutubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Intent openWebNoYoutubeApp = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (openYoutubeAppIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openYoutubeAppIntent);
        } else {
            // In case there is no youtube app, open web
            startActivity(openWebNoYoutubeApp);
        }
    }


    /********************************** FAV MOVIES OPERATIONS *************************************/


    /**
     * Use the View Model and live data to get all the data
     */
    private void checkMovieForFav() {
        ViewModelFactory factory = new ViewModelFactory(mDB, movieId);
        AddMovieViewModel viewModel = ViewModelProviders.of(this, factory).get(AddMovieViewModel.class);
        viewModel.getMovie().observe(this, favMovies -> {
            // If the movie is not in Database, then it will return null
            // as we are trying to retrieve something that is not in Database
            if (favMovies != null) {
                isMovieFav = favMovies.getIsMovieFav();
                changeFabVisibility(isMovieFav);
            } else {
                isMovieFav = false;
                changeFabVisibility(isMovieFav);
            }
        });
    }

    /**
     * onCreate() check if data is in DB
     * Accordingly show color of FAB
     *
     * @param makeFabVisible Make the fab button visible once we are able to setup
     *                       the database after activity creation
     */
    private void changeFabVisibility(boolean makeFabVisible) {
        if (!makeFabVisible) {
            // FAB not visible
            changeFAB(
                    ColorStateList.valueOf(getResources().getColor(R.color.colorAccentGRAY)),
                    getResources().getDrawable(R.drawable.unselected_star),
                    View.VISIBLE);
        } else {
            // FAB visible
            changeFAB(
                    ColorStateList.valueOf(getResources().getColor(R.color.colorBrightGreen)),
                    getResources().getDrawable(R.drawable.selected_star),
                    View.VISIBLE);
        }
    }

    /**
     * @param list       The color of the FAB button
     * @param image      The image based on if the movie is in database or not
     * @param visibility The visibility of button
     */
    void changeFAB(ColorStateList list, Drawable image, int visibility) {
        favFab.setBackgroundTintList(list);
        favFab.setImageDrawable(image);
        favFab.setVisibility(visibility);
    }


    /**
     * Just change Button Color to show use
     */
    private void toggleMovieDataDatabase() {
        if (isMovieFav == null) {
            checkMovieForFav();
            Toast.makeText(this, getResources().getString(R.string.database_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isMovieFav) {
            isMovieFav = false;
            changeFabVisibility(isMovieFav);
            //Remove From Fav DB
            doDatabaseOperation();
        } else {
            isMovieFav = true;
            changeFabVisibility(isMovieFav);
            //Add to Fav DB
            doDatabaseOperation();
        }
    }

    /**
     * Check for Database and update DB only if necessary
     */
    private void doDatabaseOperation() {
        if (isMovieFav == null) {
            checkMovieForFav();
            return;
        }

        if (isMovieFav) {
            /** Add to DB */
            addToDB();
        } else {
            /** Delete from DB */
            removeFromDB();
        }
    }

    /**
     * Add Data to DB in the end onPause or onDestroy or onBackPressed
     */
    private void addToDB() {
        ThreadExecutors
                .getThreadExecutorsInstance()
                .getDiskExecutor()
                .execute(() -> mDB.moviesDAO().addMovieToFav(generateMovieForDB(true)));
    }

    /**
     * Remove Data to DB in the end onPause or onDestroy or onBackPressed
     */
    private void removeFromDB() {
        ThreadExecutors
                .getThreadExecutorsInstance()
                .getDiskExecutor()
                .execute(() -> mDB.moviesDAO().removeFromFavourites(generateMovieForDB(false)));
    }


    /**
     * Generate FavMovies for DB operation for the Movie operations
     * If the movie is Favourite Movie in Database,
     * generate the object with isMovFav as false to remove from the database
     *
     * If the movie is not in Favourite Movie in Database,
     * generate the object with isMovFav as true to add the movie to the database
     *
     * @param isFav Is the Movie fav
     * @return
     */
    @NonNull
    private FavMovies generateMovieForDB(Boolean isFav) {
        return new FavMovies(
                mMovieObjectData.getMovieName(),
                isFav,
                mMovieObjectData.getAverageVote(),
                mMovieObjectData.getImageURL(),
                mMovieObjectData.getMovieId());
    }


    /************************************ COMMON ************************************/


    /**
     * Change the notification pane color to match chosen color
     */
    private void changeNotificationPaneColor() {
        //Change Color Of Notification Bar to match the theme and colors chosen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentGRAY));
        }
    }


    /************************************LIFECYCLE EVENTS************************************/

    @Override
    protected void onPause() {
        super.onPause();
        if (reviewOfItem != null
                && reviewDialog != null
                && isMaterialDialogOpen != null
                && isMaterialDialogOpen) {
            // To Avoid Window Leak: E/WindowManager: android.view.WindowLeaked:
            reviewDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (reviewOfItem != null
                && isMaterialDialogOpen != null
                && isMaterialDialogOpen
                && reviewDialog == null) {
            openMaterialDialog();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (reviewDialog != null) {
            if (isMaterialDialogOpen != null && isMaterialDialogOpen) {
                reviewDialog = null;
                outState.putString(MATERIAL_DIALOG_REVIEW_KEY, reviewOfItem);
            }
        }
    }


}
