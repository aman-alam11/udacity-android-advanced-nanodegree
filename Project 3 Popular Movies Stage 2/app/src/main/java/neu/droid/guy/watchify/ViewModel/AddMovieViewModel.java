package neu.droid.guy.watchify.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import neu.droid.guy.watchify.POJO.FavMovies;
import neu.droid.guy.watchify.Room.AppDatabase;

public class AddMovieViewModel extends ViewModel {

    private LiveData<FavMovies> movie;

    public AddMovieViewModel(AppDatabase mAppDatabase, String mTmdbId) {
        movie = mAppDatabase.moviesDAO().getMovieById(mTmdbId);
    }

    public LiveData<FavMovies> getMovie() {
        return movie;
    }
}
