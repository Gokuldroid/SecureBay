package com.app.security.securebay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class AppPermission extends ActionBarActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permission);
        listView = (ListView) findViewById(R.id.appsperlist);
        AppListing appList=new AppListing(this);
        listView.setAdapter(appList);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class AppListing extends BaseAdapter {

        List<ResolveInfo> list;
        Context context;
        PackageManager packageManager;
        SharedPreferences prefs;
        SharedPreferences.Editor editor;


        AppListing(Context context) {
            this.context = context;
            packageManager = context.getPackageManager();
            final PackageManager pm = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            prefs = context.getSharedPreferences("MyAppPrefs", 0);
            editor = prefs.edit();
            list = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        }


        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.single_per, parent, false);


            ImageView imageView = (ImageView) row.findViewById(R.id.imageView);
            final TextView appname = (TextView) row.findViewById(R.id.appname);


            final ResolveInfo resolveInfo = list.get(position);
            imageView.setImageDrawable(resolveInfo.loadIcon(packageManager));
            appname.setText(resolveInfo.loadLabel(packageManager));

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        PackageInfo packageInfo=getPackageManager().getPackageInfo(resolveInfo.activityInfo.packageName,PackageManager.GET_PERMISSIONS);
                        String[] requestedPermissions = packageInfo.requestedPermissions;
                        ApplicationInfo appInfo=getPackageManager().getApplicationInfo(resolveInfo.activityInfo.packageName,0);
                        int uid=appInfo.uid;

                        Set<String> set=new HashSet<String>();
                        for(int i=0;i<requestedPermissions.length;i++)
                        {
                            if(requestedPermissions[i].contains("LOCATION"))
                            {
                                set.add("Access Locations");
                            }
                            if(requestedPermissions[i].contains("NETWORK"))
                            {
                                set.add("Access Networks");
                            }
                            if(requestedPermissions[i].contains("WIFI"))
                            {
                                set.add("Access WiFi");
                            }
                            if(requestedPermissions[i].contains("BLUETOOTH"))
                            {
                                set.add("Access Bluetooth");
                            }
                            if(requestedPermissions[i].contains("SMS"))
                            {
                                set.add("Access SMS");
                            }
                            if(requestedPermissions[i].contains("CALL"))
                            {
                                set.add("Access CALL");
                            }
                            if(requestedPermissions[i].contains("CONTACTS"))
                            {
                                set.add("Access Contacts");
                            }
                            if(requestedPermissions[i].contains("TASKS"))
                            {
                                set.add("Access Tasks");
                            }
                            if(requestedPermissions[i].contains("REBOOT"))
                            {
                                set.add("Can Reboot");
                            }
                            if(requestedPermissions[i].contains("SHUTDOWN"))
                            {
                                set.add("Can Reboot");
                            }
                        }
                        Log.d("set", set.toString());
                        String [] per=new String[set.size()];
                        Iterator iterator=set.iterator();
                        int i=0;
                        while (iterator.hasNext())
                        {
                            per[i++]= (String) iterator.next();
                        }
                        if(set.size()==0)
                        {
                            per=new String[1];
                            per[0]="No Permission found";
                        }

                        final double data=TrafficStats.getUidRxBytes(uid)/1024;
                        new MaterialDialog.Builder(context)
                                .title("Is this app safe ?")
                                .items(per)
                                .positiveText("Yes")
                                .negativeText("No")
                                .neutralText("Data : "+data +" MB")
                                .autoDismiss(false)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
                                        dialog.dismiss();
                                        new MaterialDialog.Builder(context)
                                                .title("Uninstall")
                                                .content("Do you want to uninstall "+resolveInfo.loadLabel(packageManager)+" ?")
                                                .positiveText("Yes")
                                                .negativeText("No")
                                                .callback(new MaterialDialog.ButtonCallback() {
                                                    @Override
                                                    public void onPositive(MaterialDialog dialog) {
                                                        super.onPositive(dialog);
                                                        Uri packageUri = Uri.parse("package:"+resolveInfo.activityInfo.packageName);
                                                        Intent uninstallIntent =
                                                                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                                                        startActivity(uninstallIntent);
                                                    }

                                                    @Override
                                                    public void onNegative(MaterialDialog dialog) {
                                                        super.onNegative(dialog);
                                                    }
                                                })
                                                .show();

                                    }

                                    @Override
                                    public void onNeutral(MaterialDialog dialog) {

                                    }
                                })
                                .show();

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        }
    }
}
