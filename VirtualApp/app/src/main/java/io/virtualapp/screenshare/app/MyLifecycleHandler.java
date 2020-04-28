package io.virtualapp.screenshare.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import javax.security.auth.login.LoginException;

import io.virtualapp.home.NewHomeActivity;
import io.virtualapp.screenshare.module.screenshare.SenderActivity;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "MyLifecycleHandler";
    private int foregroundActivity = 0;
    private SenderActivity senderActivity;
    private NewHomeActivity newHomeActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated: " + activity.getLocalClassName());
        if (activity.getLocalClassName().equals("io.virtualapp.screenshare.module.screenshare.SenderActivity")) {
            Log.i(TAG, "onActivityCreated: 设置SenderActivity");
            senderActivity = (SenderActivity) activity;
        }
        if (activity.getLocalClassName().equals("io.virtualapp.home.NewHomeActivity")) {
            Log.i(TAG, "onActivityCreated: 设置NewHomeActivity");
            newHomeActivity = (NewHomeActivity) activity;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i(TAG, "onActivityStarted: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        foregroundActivity++;
        Log.i(TAG, "onActivityResumed: " + activity.getLocalClassName() + "number: " + foregroundActivity);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        foregroundActivity--;
        Log.i(TAG, "onActivityPaused: " + activity.getLocalClassName() + "number: " + foregroundActivity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i(TAG, "onActivityStopped: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i(TAG, "onActivityDestroyed: " + activity.getLocalClassName());
        if (activity.equals(newHomeActivity)) {
            if (senderActivity != null) {
                senderActivity.clickStopScreenShareButton();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

}
