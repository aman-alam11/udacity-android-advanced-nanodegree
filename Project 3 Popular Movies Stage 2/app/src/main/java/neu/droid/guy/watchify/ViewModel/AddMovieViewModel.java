package neu.droid.guy.watchify.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.Room.AppDatabase;

public class AddMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public AddMovieViewModel(AppDatabase mAppDatabase, String mTmdbId) {
        movie = mAppDatabase.moviesDAO().getMovieById(mTmdbId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
