package com.app.security.securebay;

/**
 * Created by goku on 22-02-2015.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by goku on 07-02-2015.
 */
public class PasswordActivity extends ActionBarActivity {


    private Toolbar toolbar;
    String firsttime="false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WinDroid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b=getIntent().getExtras();

        final SharedPreferences sharedPrefs=getSharedPreferences("MyAppPrefs",0);
        final SharedPreferences.Editor editor=sharedPrefs.edit();
        // subsequent launches will get this value as false


        final EditText admin_old=(EditText)findViewById(R.id.admin_edit);
        final EditText admin_new=(EditText)findViewById(R.id.admin_newedit);
        final EditText guest_new=(EditText)findViewById(R.id.guest_edit);

        if(b!=null)
        {
            firsttime=b.getString("first");
        }

        if(firsttime.equals("true"))
        {
            findViewById(R.id.admin_edit).setVisibility(View.INVISIBLE);
            findViewById(R.id.admin_text).setVisibility(View.INVISIBLE);
            editor.putString("admin_password","");
            editor.putString("guest_password","");
            editor.commit();
        }


        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((admin_old.getText().toString().equals(sharedPrefs.getString("admin_password","")))||firsttime.equals("true"))
                {

                    editor.putString("admin_password",admin_new.getText().toString());
                    editor.putString("guest_password",guest_new.getText().toString());
                    editor.commit();
                    Toast.makeText(PasswordActivity.this,"Password set",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(PasswordActivity.this,"Wrong Password",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}

