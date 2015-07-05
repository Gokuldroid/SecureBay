package com.app.security.securebay;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

/**
 * Created by goku on 21-02-2015.
 */
public class ChangeReceiver extends BroadcastReceiver {

    Context c;
    SharedPreferences sharedPrefs;
    @Override
    public void onReceive(Context context, Intent intent) {

        c=context;

        String status = NetworkUtil.getConnectivityStatusString(context);

        sharedPrefs = context.getSharedPreferences("MyAppPrefs", 0);


        if(sharedPrefs.getString("data_lock","true").equals("true")&&sharedPrefs.getString("login_type","admin").equals("guest")) {


            disableWIFI();
            disableBLUETOOTH();

        }

    }

    private void disableBLUETOOTH() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    private void disableWIFI() {
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(false);

       

    }

}
