package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Pulikutties on 2/6/2015.
 */
public class AppLockService extends Service {

    List<ActivityManager.RunningTaskInfo> mTestList = new ArrayList<>();
    private String mLastPackageName;
    private ActivityManager mActivityManager;
    SharedPreferences preferences,sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        preferences=this.getSharedPreferences("MyAppPrefs",0);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        Log.i("create", "created");
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TimerTask scanTask;
        final Handler handler = new Handler();
        Timer t = new Timer();

        scanTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        checkPackageChanged();
                    }
                });
            }
        };
        t.schedule(scanTask, 300, 1000);
        return START_STICKY;
    }

    private void checkPackageChanged() {
        final String packageName = getTopPackageName();

        if (!packageName.equals(mLastPackageName)) {
            Log.d("App", "appchanged " + " (" + mLastPackageName + ">"
                    + packageName + ")");

            onAppClose(mLastPackageName, packageName);
            onAppOpen(packageName, mLastPackageName);
        }

        // prepare for next call
        mLastPackageName = packageName;
        // mLastCompleteName = completeName;
    }

    private void onAppClose(String close, String open) {

    }

    private void onAppOpen(final String open, final String close) {
        try {
            ApplicationInfo applicationInfo=getPackageManager().getApplicationInfo(open,0);
            String title= (String) applicationInfo.loadLabel(getPackageManager());
            //  Toast.makeText(getApplicationContext(),title +"  " +preferences.getString(title,"Hello") + preferences.getString("login_type","Not") ,Toast.LENGTH_SHORT).show();
            if (preferences.getString("login_type","").equals("guest") && preferences.getString(title,"").equals("true")) {
                Intent intent = new Intent(this, LockScreenApp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String getTopPackageName() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } else {
            final List<ActivityManager.RunningAppProcessInfo> pis = mActivityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo pi : pis) {
                if (pi.pkgList.length == 1) return pi.pkgList[0];
            }
        }
        return "";
    }
}

