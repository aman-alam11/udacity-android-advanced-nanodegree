package neu.droid.guy.watchify.Lambdas;

import java.util.List;

import neu.droid.guy.watchify.POJO.Movie;

/**
 * Lambda to check if data is invalid
 */
@FunctionalInterface
public interface checkMovieDataValidity {
    boolean isDataInValid(List<Movie> data);
}
