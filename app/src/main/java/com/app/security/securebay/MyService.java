package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class MyService extends Service {
    BroadcastReceiver mReceiver;

    TelephonyManager tManager;

    // Intent myIntent;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {



        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);


    }


    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        super.onStart(intent, startId);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}

