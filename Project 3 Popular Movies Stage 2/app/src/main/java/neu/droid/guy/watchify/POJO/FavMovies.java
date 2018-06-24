package neu.droid.guy.watchify.POJO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/** Adding annotation @Entity makes ROOM generate a table call FavMovies */
/** To give a different name to database table, we can optionally add a table name (tableName = "myName")*/

@Entity(tableName = "FavouritesTable")
public class FavMovies {

    /**
     *  Add a primary key to the table
     *  We have to add a primary key to the table
     *  autoGenerate is false by default
     *  */
    @PrimaryKey @NonNull
    private String tmdbMovieID;


    private String mMovieName;
    private Boolean mIsMovieFav;
    private String mMovieRating;
    private String mImageUrl;


    /**
     * Default constructor
     * Room can have only 1 constructor
     * If we are having 2 constructors in POJO,
     * we can add @Ignore annotation to the one we want to ignore
     */
    public FavMovies(String mMovieName, Boolean mIsMovieFav, String mMovieRating,
                     String mImageUrl, String tmdbMovieID) {
        this.mMovieName = mMovieName;
        this.mIsMovieFav = mIsMovieFav;
        this.mMovieRating = mMovieRating;
        this.mImageUrl = mImageUrl;
        this.tmdbMovieID = tmdbMovieID;
    }

    /**
     * Default constructors
     */


    public String getMovieName() {
        return mMovieName;
    }

    public void setMovieName(String mMovieName) {
        this.mMovieName = mMovieName;
    }

    public Boolean getIsMovieFav() {
        return mIsMovieFav;
    }

    public void setIsMovieFav(Boolean mIsMovieFav) {
        this.mIsMovieFav = mIsMovieFav;
    }

    public String getMovieRating() {
        return mMovieRating;
    }

    public void setMovieRating(String mMovieRating) {
        this.mMovieRating = mMovieRating;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getTmdbMovieID() {
        return tmdbMovieID;
    }

    public void setTmdbMovieID(String tmdbMovieID) {
        this.tmdbMovieID = tmdbMovieID;
    }
}
