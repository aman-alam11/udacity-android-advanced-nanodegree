package neu.droid.guy.watchify.Lambdas;

import java.util.List;

import neu.droid.guy.watchify.POJO.Video;

/**
 * Extract url keys from Array List of Video Objects
 */
@FunctionalInterface
public interface GetKeysLambda {
    List<String> getAllKeys(List<Video> vid);
}

