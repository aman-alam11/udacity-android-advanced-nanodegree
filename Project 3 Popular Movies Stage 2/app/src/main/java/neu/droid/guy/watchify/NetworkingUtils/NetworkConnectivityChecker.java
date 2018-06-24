package neu.droid.guy.watchify.NetworkingUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.Objects;

public class NetworkConnectivityChecker {
    /**
     * Check for internet
     * */
    public static boolean isInternetConnectivityAvailable(Context context){

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Objects.requireNonNull(manager).getActiveNetworkInfo() != null) {
            // Means not in airplane mode
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info.isConnectedOrConnecting();
        }
        return false;
    }
}
