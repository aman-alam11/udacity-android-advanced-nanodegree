package neu.droid.guy.watchify.NetworkingUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import static java.lang.System.exit;

public class ErrorHandler {

    public static void showErrorMessageAndExitApp(final Context context, String Message) {
        new MaterialDialog.Builder(context).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                exit(0);
            }
        }).title("ERROR")
                .content(Message)
                .negativeText("OK")
                .show();
    }

    public static void showErrorMessage(final Context context, String Message) {
        new MaterialDialog.Builder(context)
                .title("ERROR")
                .content(Message)
                .negativeText("OK")
                .show();
    }
}
