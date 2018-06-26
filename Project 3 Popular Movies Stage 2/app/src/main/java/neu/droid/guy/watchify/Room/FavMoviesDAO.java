package neu.droid.guy.watchify.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import neu.droid.guy.watchify.POJO.Movie;

@Dao
public interface FavMoviesDAO {
    /**
     * QUERIES
     */
    @Query("SELECT * FROM FavouriteTable")
    LiveData<List<Movie>> getAllFavMovies();

    @Query("SELECT * FROM FavouriteTable where id = :id")
    LiveData<Movie> getMovieById(String id);

    /**
     * INSERTIONS
     */
    @Insert
    void addMovieToFav(Movie movieToAdd);

    /**
     * UPDATES
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateObject(Movie movieToUpdate);


    /**
     * DELETIONS
     */
    @Delete
    void removeFromFavourites(Movie movies);
}
