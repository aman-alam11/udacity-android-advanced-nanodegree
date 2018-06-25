package neu.droid.guy.watchify.NetworkingUtils;

import android.annotation.SuppressLint;
import android.content.Context;

import neu.droid.guy.watchify.BuildConfig;
import neu.droid.guy.watchify.R;

public class BuildUrl {

    /**
     * Get the popular movies from API
     */
    public static String getPopularMovieEndPoint() {
        return "http://api.themoviedb.org/3/movie/popular?api_key=" + getApiKey();
    }


    /**
     * Get the top rated movies from API
     */
    public static String getTopRatedMovieEndPoint() {
        return "http://api.themoviedb.org/3/movie/top_rated?api_key=" + getApiKey();
    }

    /**
     * Get the image/poster/backdrop from API
     */
    public static String getImagePosterEndPoint(String endUrl, String dimensionWidth) {
        return "http://image.tmdb.org/t/p/" + dimensionWidth + endUrl;
    }

    /**
     * Get API_KEY from Resources
     */
    private static String getApiKey() {
        return BuildConfig.MY_MOVIE_DB_API_KEY;
    }


    /**
     * Get Movie Trailer Links and data
     */
    public static String getVideoDetails(String movieId){
        return "http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + getApiKey();
    }


    /**
     * Append the extracted id of video to make it a complete Youtube URL
     */
    public static String getYoutubeURL(String videoKey){
        return "https://www.youtube.com/watch?v=" + videoKey;
    }



    public static String getReviewsForMovie(String movieID){
        return "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key=" + getApiKey();
    }
}