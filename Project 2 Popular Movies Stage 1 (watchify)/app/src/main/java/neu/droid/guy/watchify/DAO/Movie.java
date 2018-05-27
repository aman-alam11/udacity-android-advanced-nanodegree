package neu.droid.guy.watchify.DAO;

import android.os.Parcel;
import android.os.Parcelable;

import neu.droid.guy.watchify.NetworkingUtils.BuildUrl;
import neu.droid.guy.watchify.NetworkingUtils.GetImageDimensions;

public class Movie implements Parcelable {
    private String original_title;
    private String vote_average;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private String original_language;
    private String release_date;
    private String adult;

    public Movie(Parcel in) {
        original_title = in.readString();
        vote_average = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        original_language = in.readString();
        release_date = in.readString();
        adult = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /*
     * Setters and Getters
     * */
    public String getMovieName() {
        return original_title;
    }

    public void setMovieName(String name) {
        this.original_title = name;
    }

    public String getAverageVote() {
        return vote_average;
    }

    public void setAverageVote(String averageVote) {
        this.vote_average = averageVote;
    }

    public String getImageURL() {

        return BuildUrl.getImagePosterEndPoint(poster_path, GetImageDimensions.MEDIUM.returnImageSize());
    }

    public String getBackdropImageURL() {
        return BuildUrl.getImagePosterEndPoint(backdrop_path,
                GetImageDimensions.MEDIUM.returnImageSize());
    }

    public String getMovieDescription() {
        return overview;
    }

    public void setMovieDescription(String description) {
        this.overview = description;
    }

    public String getMovieLanguage() {
        return original_language;
    }

    public void setMovieLanguage(String movieLanguage) {
        this.original_language = movieLanguage;
    }

    public String getMovieRestrictions() {
        if(this.adult.toLowerCase().equals("true")){
            return "Yes";
        } else {
            return "No";
        }
    }

    public void setMovieRestrictions(String mMovieRestrictions) {
        this.adult = mMovieRestrictions;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.release_date = mReleaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
