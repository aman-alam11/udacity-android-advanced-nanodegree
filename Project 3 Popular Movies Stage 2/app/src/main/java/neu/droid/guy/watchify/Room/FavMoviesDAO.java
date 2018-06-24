package neu.droid.guy.watchify.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import neu.droid.guy.watchify.POJO.FavMovies;

@Dao
public interface FavMoviesDAO {
    /**
     * QUERIES
     */
    @Query("SELECT * FROM FavouritesTable")
    LiveData<List<FavMovies>> getAllFavMovies();

    @Query("SELECT * FROM FavouritesTable where tmdbMovieID = :id")
    LiveData<FavMovies> getMovieById(String id);

    /**
     * INSERTIONS
     */
    @Insert
    void addMovieToFav(FavMovies movieToAdd);

    /**
     * UPDATES
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateObject(FavMovies movieToUpdate);


    /**
     * DELETIONS
     */
    @Delete
    void removeFromFavourites(FavMovies movies);
}
