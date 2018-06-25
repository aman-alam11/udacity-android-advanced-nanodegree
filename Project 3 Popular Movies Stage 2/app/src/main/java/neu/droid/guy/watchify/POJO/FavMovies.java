package neu.droid.guy.watchify.POJO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.GetImageDimensions;

/**
 * Adding annotation @Entity makes ROOM generate a table call FavMovies
 */

/**
 * To give a different name to database table, we can optionally add a table name (tableName = "myName")
 */

@Entity(tableName = "FavouritesTable")
public class FavMovies implements Parcelable{

    /**
     * Add a primary key to the table
     * We have to add a primary key to the table
     * autoGenerate is false by default
     */
    @PrimaryKey
    @NonNull
    public String id;

    public String original_title;
    public String vote_average;
    public String overview;
    public String poster_path;
    public String backdrop_path;
    public String original_language;
    public String release_date;
    public String adult;



    /**
     * Default constructor
     * Room can have only 1 constructor
     * If we are having 2 constructors in POJO,
     * we can add @Ignore annotation to the one we want to ignore
     */
    public FavMovies(String original_title, String id, String vote_average,
                     String overview, String poster_path, String backdrop_path,
                     String original_language, String release_date, String adult) {
        this.original_title = original_title;
        this.id = id;
        this.vote_average = vote_average;
        this.overview = overview;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.original_language = original_language;
        this.release_date = release_date;
        this.adult = adult;
    }

    @Ignore
    protected FavMovies(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        vote_average = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        original_language = in.readString();
        release_date = in.readString();
        adult = in.readString();
    }

    public static final Creator<FavMovies> CREATOR = new Creator<FavMovies>() {
        @Override
        public FavMovies createFromParcel(Parcel in) {
            return new FavMovies(in);
        }

        @Override
        public FavMovies[] newArray(int size) {
            return new FavMovies[size];
        }
    };

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getMovieId(){
        return this.id;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(vote_average);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(original_language);
        dest.writeString(release_date);
        dest.writeString(adult);
    }

}
