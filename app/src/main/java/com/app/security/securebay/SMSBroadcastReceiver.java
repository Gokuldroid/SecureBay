package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";
    SharedPreferences sharedPrefs;


    Context c;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent recieved: " + intent.getAction());
        sharedPrefs = context.getSharedPreferences("MyAppPrefs", 0);


        c=context;
        if (intent.getAction() == SMS_RECEIVED) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    Log.i(TAG, "Message recieved: " + messages[0].getMessageBody());

                    if (messages[0].getDisplayMessageBody().contains("wipe") && messages[0].getDisplayMessageBody().contains(sharedPrefs.getString("admin_password", null))) {
                        int i = messages[0].getDisplayMessageBody().lastIndexOf(":") + 1;
                        String direct = messages[0].getDisplayMessageBody().substring(i, messages[0].getDisplayMessageBody().length());
                        wipeData(context, direct);
                    }
                    if (messages[0].getDisplayMessageBody().contains("location") && messages[0].getDisplayMessageBody().contains(sharedPrefs.getString("admin_password", null))) {
                        sendLocation(context, messages[0].getDisplayOriginatingAddress());
                    }
                    if(messages[0].getDisplayMessageBody().contains("your_location"))
                    {

                        sendLocation(context,messages[0].getDisplayOriginatingAddress());
                    }
                    if (messages[0].getDisplayMessageBody().contains("Lat"))
                    {
                        getLocation(context,messages[0].getDisplayMessageBody());
                    }
                }
            }
        }
    }

    private void sendLocation(Context context,String phno) {


        GPStracker gps;
        gps = new GPStracker(context);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
           // Toast.makeText(context, "Your Location is - \nLat: " + latitude + "\nLong:  " + longitude, Toast.LENGTH_LONG).show();
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phno, null, "http://maps.google.com/maps/place/"  + latitude +","+longitude, null, null);
                Toast.makeText(context, "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context,
                        "SMS faild, please try again.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    private void getLocation(Context context,String s) {
        Toast.makeText(context, "location get", Toast.LENGTH_SHORT).show();
        int i=s.indexOf(":");
        int j=s.lastIndexOf(":");

        String lat=s.substring(i+1,i+8).trim();
        String lng=s.substring(j+1,j+8).trim();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps/place/"+lat+","+lng));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void wipeData(Context context, String direct) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/SecureBayFiles");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
            dir.delete();
        }

        if (direct.length() > 0) {
            File dir2 = new File(Environment.getExternalStorageDirectory() + "/" + direct.trim());
            if (dir2.isDirectory()) {
                String[] children = dir2.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir2, children[i]).delete();
                }
                dir2.delete();

            }
        }
        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
    }


}