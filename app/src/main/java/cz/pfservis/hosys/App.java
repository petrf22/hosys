package cz.pfservis.hosys;

import android.app.Application;
import android.content.Context;

/**
 * Created by petr on 23.11.16.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getCtx() {
        return mContext;
    }
}
