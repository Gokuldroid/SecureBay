package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

/**
 * Created by goku on 06-02-2015.
 */
public class MainActivity extends ActionBarActivity {
    private DrawerLayout drawerLayoutt;
    private View drawerView;



    private Toolbar toolbar;
    ListView listView;

    ActionBarDrawerToggle actionBarDrawerToggle;


    private String[] navigationDrawerItems = {"Change Password","App permissions"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);



        this.startService(new Intent(this, MyService.class));
        this.startService(new Intent(this,AppLockService.class));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SecureBay");







        SharedPreferences sharedPrefs=getSharedPreferences("MyAppPrefs",0);
//        Toast.makeText(this, "finsh"+sharedPrefs.getString("login_type","admin"), Toast.LENGTH_SHORT).show();

        if(sharedPrefs.getString("login_type","admin").equals("guest"))
        {

            finish();
        }
        // The default value is true as the preference does not exist yet
        boolean isFirstLaunch=sharedPrefs.getBoolean("firstLaunch",true);
        if(isFirstLaunch)
        {
            // An editor so you can write the preference
            SharedPreferences.Editor editor=sharedPrefs.edit();
            // subsequent launches will get this value as false
            editor.putBoolean("firstLaunch",false);
            editor.commit();
            call(10);
        }


        drawerLayoutt = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayoutt.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        listView = (ListView) findViewById(R.id.left_list);
        // set up the drawer's list view with items and click listener


        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navigationDrawerItems));


        listView.setOnItemClickListener(new DrawerItemClickListener());


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutt, toolbar, R.string.app_name, R.string.app_name);
        drawerLayoutt.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        Fragment fragment = new DummyFragment(getBaseContext());


        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments


        // update selected item and title, then close the drawer
        listView.setItemChecked(position, true);

        call(position);

        drawerLayoutt.closeDrawer(drawerView);


    }


    public void call(int flag) {
        switch (flag) {
            case 10:startActivity(new Intent(this,PasswordActivity.class).putExtra("first","true"));
                break;
            case 0:startActivity(new Intent(this,PasswordActivity.class).putExtra("first","false"));
                break;
            case 1:startActivity(new Intent(this,AppPermission.class).putExtra("first","false"));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle("");
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @SuppressLint("ValidFragment")
    public static class DummyFragment extends Fragment {

        View rootView;
        ListView listView;
        Context context;
        CheckBox c;
        SharedPreferences pref;
        SharedPreferences.Editor e;


        public DummyFragment(Context c) {
            this.context=c;
            pref=c.getSharedPreferences("MyAppPrefs",0);
            e=pref.edit();
            // Empty constructor required for fragment subclasses


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.activity_app_list, container, false);
            listView = (ListView) rootView.findViewById(R.id.applist);
            c=(CheckBox)rootView.findViewById(R.id.data_check);
            if(pref.getString("data_lock","true").equals("true"))
            {
                c.setChecked(true);
            }
            else
            {
                c.setChecked(false);
            }
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        e.putString("data_lock","true");
                        e.commit();
                    }
                    else
                    {
                        e.putString("data_lock","false");
                        e.commit();
                    }
                }
            });
            AppListAdapter appList=new AppListAdapter(this.context);
            listView.setAdapter(appList);


            return rootView;
        }


    }


}

