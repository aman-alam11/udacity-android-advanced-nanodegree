package neu.droid.guy.watchify.FavActivity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.POJO.FavMovies;
import neu.droid.guy.watchify.R;
import neu.droid.guy.watchify.Room.AppDatabase;
import neu.droid.guy.watchify.ViewModel.AddMovieViewModel;
import neu.droid.guy.watchify.ViewModel.MainViewModel;
import neu.droid.guy.watchify.ViewModel.ThreadExecutors;
import neu.droid.guy.watchify.ViewModel.ViewModelFactory;

public class ShowFavMovies extends AppCompatActivity implements FavMoviesAdapter.onItemClicked {
    @BindView(R.id.fav_movies_pb)
    ProgressBar favProgressBar;
    @BindView(R.id.fav_movies_rv)
    RecyclerView favRecyclerView;
    @BindView(R.id.no_data_screen_text)
    TextView noDataTextView;


    /**
     * Variable Initialization
     */
    private AppDatabase mDB;
    private FavMoviesAdapter mFavMoviesAdapter;
    private String SNACKBAR_VISIBLE_KEY = "SNACKBAR_VISIBLE";
    Boolean isSnackBarVisible;
    private Snackbar snackBar;
    private static final int SNACKBAR_PADDING = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_movies);
        ButterKnife.bind(this);
        GridLayoutManager manager = new GridLayoutManager(this,
                1,
                GridLayoutManager.VERTICAL,
                false);
        favRecyclerView.setLayoutManager(manager);

        /** For First Time */
        if (savedInstanceState == null) {
            showSnackBar();
            snackBar.show();
        }
        setUpViewModel();
    }

    private void showSnackBar() {
        isSnackBarVisible = true;
        snackBar = Snackbar.make(getWindow().getDecorView(),
                getResources().getString(R.string.snackbar_tap_message),
                Snackbar.LENGTH_INDEFINITE);
        snackBar.getView().setPadding(SNACKBAR_PADDING, 0, SNACKBAR_PADDING, SNACKBAR_PADDING);
        snackBar.setAction(getResources().getString(R.string.snackbar_button_title), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSnackBarVisible = false;
                snackBar.dismiss();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isSnackBarVisible == null) {
            isSnackBarVisible = false;
        }
        outState.putBoolean(SNACKBAR_VISIBLE_KEY, isSnackBarVisible);
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(SNACKBAR_VISIBLE_KEY)) {
            showSnackBar();
            snackBar.show();
        }
    }

    private void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getListOfMovies().observe(this, new Observer<List<FavMovies>>() {
            @Override
            public void onChanged(@Nullable List<FavMovies> favMovies) {
                if (favMovies == null || favMovies.size() == 0) {
                    Toast.makeText(ShowFavMovies.this, "No Favourite Movies", Toast.LENGTH_SHORT).show();
                    noDataTextView.setVisibility(View.VISIBLE);
                    if (snackBar != null && snackBar.isShown()) {
                        isSnackBarVisible = false;
                        snackBar.dismiss();
                    }
                }
                mFavMoviesAdapter = new FavMoviesAdapter(favMovies, ShowFavMovies.this);
                favRecyclerView.setAdapter(mFavMoviesAdapter);
                favRecyclerView.setVisibility(View.VISIBLE);
                favProgressBar.setVisibility(View.INVISIBLE);
                favRecyclerView.setHasFixedSize(false);
            }
        });
    }

    @Override
    public void getMovieClicked(FavMovies movieToDelete) {
        mDB = AppDatabase.getInstance(getApplicationContext());
        ThreadExecutors
                .getThreadExecutorsInstance()
                .getDiskExecutor()
                .execute(() -> mDB.moviesDAO().removeFromFavourites(movieToDelete));
    }

}