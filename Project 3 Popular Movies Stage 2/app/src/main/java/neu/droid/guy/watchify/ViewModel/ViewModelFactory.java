package neu.droid.guy.watchify.ViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import neu.droid.guy.watchify.Room.AppDatabase;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private final AppDatabase mAppDatabase;
    private final String mTmdbId;

    public ViewModelFactory(AppDatabase mAppDatabase, String mTmdbId) {
        this.mAppDatabase = mAppDatabase;
        this.mTmdbId = mTmdbId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMovieViewModel(mAppDatabase,mTmdbId);
    }
}
