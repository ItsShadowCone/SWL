package eu.daclemens.swl;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";

    // experimentally determined
    private static int ON_RESUME_DELAY = 750;
    private long lastResume;

    @Override
    protected void onResume() {
        super.onResume();
        lastResume = System.currentTimeMillis();
    }

    public void startApp(String packageName) {
        long currentMillis = System.currentTimeMillis();
        long millisSinceLastResume = currentMillis - lastResume;
        Log.d(TAG, String.format("Attempting start %d", millisSinceLastResume));
        if (millisSinceLastResume < ON_RESUME_DELAY) {
            // Workaround an issue where the WM launch animation is clobbered when finishing the
            // recents animation into launcher. Defer launching the activity until Launcher is
            // next resumed.

            // See https://github.com/GrapheneOS/platform_packages_apps_Launcher3/blob/a167969c6a18395c0092de122852650aa5ad2c20/src/com/android/launcher3/Launcher.java#L2160-L2176
            // for more details

            // EDIT: For some reason resume() is still triggered before the launch. Added the delay to opportunistically fix things
            Log.d(TAG, "Using workaround");
            new Handler(Looper.getMainLooper()).postDelayed(() -> startApp(packageName), ON_RESUME_DELAY-millisSinceLastResume);
            return;
        }

        Log.d(TAG, "Starting app");
        startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
    }
}
