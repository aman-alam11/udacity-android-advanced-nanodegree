package neu.droid.guy.watchify.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import neu.droid.guy.watchify.POJO.FavMovies;
import neu.droid.guy.watchify.Room.AppDatabase;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavMovies>> listOfMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        listOfMovies = AppDatabase.getInstance(this.getApplication()).moviesDAO().getAllFavMovies();
        Log.e("TAG MainViewModel: ", listOfMovies.toString());
    }

    public LiveData<List<FavMovies>> getListOfMovies() {
        return listOfMovies;
    }
}
