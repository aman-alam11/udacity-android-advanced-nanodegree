package neu.droid.guy.watchify.POJO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Adding annotation @Entity makes ROOM generate a table call FavMovies
 */

/**
 * To give a different name to database table, we can optionally add a table name (tableName = "myName")
 */

@Entity(tableName = "FavouritesTable")
public class FavMovies {

    /**
     * Add a primary key to the table
     * We have to add a primary key to the table
     * autoGenerate is false by default
     */
    @PrimaryKey
    @NonNull
    private String tmdbMovieID;


    private String mMovieName;
    private Boolean mIsMovieFav;
    private String mMovieRating;
    private String mImageUrl;
    private String mVoteAverage;
    private String mMovieOverview;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOriginalLanguage;
    private String mReleaseDate;
    private String mIsAdult;


    /**
     * Default constructor
     * Room can have only 1 constructor
     * If we are having 2 constructors in POJO,
     * we can add @Ignore annotation to the one we want to ignore
     */
    public FavMovies(String mMovieName, Boolean mIsMovieFav, String mMovieRating,
                     String mImageUrl, String tmdbMovieID, String voteAverage,
                     String movieOverview, String posterPath, String backdropPath,
                     String originalLanguage, String releaseDate, String isMovieAdult) {
        this.mMovieName = mMovieName;
        this.mIsMovieFav = mIsMovieFav;
        this.mMovieRating = mMovieRating;
        this.mImageUrl = mImageUrl;
        this.tmdbMovieID = tmdbMovieID;
        this.mVoteAverage = voteAverage;
        this.mMovieOverview = movieOverview;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mOriginalLanguage = originalLanguage;
        this.mReleaseDate = releaseDate;
        this.mIsAdult = isMovieAdult;
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

    public String getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmMovieOverview() {
        return mMovieOverview;
    }

    public void setmMovieOverview(String mMovieOverview) {
        this.mMovieOverview = mMovieOverview;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmBackdropPath() {
        return mBackdropPath;
    }

    public void setmBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public String getmOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmIsAdult() {
        return mIsAdult;
    }

    public void setmIsAdult(String mIsAdult) {
        this.mIsAdult = mIsAdult;
    }
}
