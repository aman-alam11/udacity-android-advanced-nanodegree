package neu.droid.guy.watchify.NetworkingUtils;


import com.android.volley.toolbox.JsonObjectRequest;

@FunctionalInterface
public interface NetworkRequest {
    JsonObjectRequest makeNetworkRequest(String url);
}