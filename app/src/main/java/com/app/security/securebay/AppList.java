package com.app.security.securebay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.Switch;

import java.util.List;

/**
 * Created by goku on 07-02-2015.
 */
public class AppList extends ActionBarActivity
{
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        listView = (ListView) findViewById(R.id.applist);
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
        final PackageManager pm= context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        prefs=context.getSharedPreferences("MyAppPrefs",0);

         editor=prefs.edit();
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
        View row= layoutInflater.inflate(R.layout.single_row, parent, false);



        ImageView imageView= (ImageView) row.findViewById(R.id.imageView);
        final TextView appname= (TextView) row.findViewById(R.id.appname);

         final Switch s=(Switch)row.findViewById(R.id.switchView);





        ResolveInfo resolveInfo=list.get(position);
        imageView.setImageDrawable(resolveInfo.loadIcon(packageManager));
        appname.setText(resolveInfo.loadLabel(packageManager));





        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                 if(prefs.getString(appname.getText().toString(),"false").equals("true"))
                {
                   editor.putString(appname.getText().toString(),"false");
                   editor.commit();
                   s.setChecked(false);

                }
                  else
                {
                     editor.putString(appname.getText().toString(),"true");
                     editor.commit();
                     s.setChecked(true);
                }

            //    Toast.makeText(context,prefs.getString(appname.getText().toString(),"false")+""+appname.getText(),Toast.LENGTH_SHORT).show();


            }
        });
        if(prefs.getString(appname.getText().toString(),"false").equals("true"))
        {
            s.setChecked(true);
        }
        else
        {
            s.setChecked(false);
        }

        return row;
    }
}
