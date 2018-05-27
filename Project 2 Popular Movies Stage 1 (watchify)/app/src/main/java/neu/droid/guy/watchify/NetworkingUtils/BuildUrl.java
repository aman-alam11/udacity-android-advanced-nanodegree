package neu.droid.guy.watchify.NetworkingUtils;

import android.annotation.SuppressLint;
import android.content.Context;

import neu.droid.guy.watchify.R;

public class BuildUrl {

    @SuppressLint("StaticFieldLeak")
    static Context mContext;

    // Handle key using Manifest
//    private static final String URL_SCHEME = "http";
//    private static final String TMDB_BASE_AUTHORITY = "api.themoviedb.org";

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
     * Call this function from activity to deliver context
     */
    public static void getContext(Context context) {
        mContext = context;
    }


    /**
     * Get API_KEY from Resources
     */
    private static String getApiKey() {
        return mContext.getResources().getString(R.string.api_key);
    }


//    private static void build(String endPointType, String API_KEY) {
//        //Since Volley takes in String, this doesn't make sense
//        Uri buildUri = new Uri.Builder()
//                .scheme(URL_SCHEME)
//                .authority(TMDB_BASE_AUTHORITY)
//                .appendEncodedPath("3")
//                .appendEncodedPath("movie")
//                .appendEncodedPath(endPointType)
//                .appendQueryParameter("api_key", API_KEY)
//                .build();
//
//    }

}