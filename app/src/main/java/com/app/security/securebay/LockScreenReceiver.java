package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by goku on 04-02-2015.
 */
public class LockScreenReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {


            // do whatever you need to do here
            //wasScreenOn = false;
            Intent intent11 = new Intent(context,LockScreenMain.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent11);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Intent intent11 = new Intent(context,LockScreenMain.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



        }

        else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent intent11 = new Intent(context,LockScreenMain.class);

            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent11);


        }

    }
}