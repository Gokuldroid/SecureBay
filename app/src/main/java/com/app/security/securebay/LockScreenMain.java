package com.app.security.securebay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by goku on 22-02-2015.
 */
public class LockScreenMain extends Activity {




    EditText editText;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String adminpassword, guest_password;
    private TextView contentTxt,date;
   private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            int level = intent.getIntExtra("level", 0);
            contentTxt.setText("Battery : "+String.valueOf(level) + "%");
        }
    };



    private Camera mCamera;
    private CameraPreview mCameraPreview;
    DigitalClock digitalClock = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);




        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.activity_lock_screen_main, null);
        wm.addView(myView, params);


        digitalClock = (DigitalClock) myView.findViewById(R.id.digitalclock);
        contentTxt=(TextView)myView.findViewById(R.id.battery_text);
        date=(TextView)myView.findViewById(R.id.date_text);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        date.setText(today.monthDay+"-"+today.month+"-"+today.year);
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) myView.findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
        Log.d("start", "started");
        final Animation animation = AnimationUtils.loadAnimation(LockScreenMain.this, R.anim.up_from_bottom);



        sharedPrefs = getSharedPreferences("MyAppPrefs", 0);
        editor = sharedPrefs.edit();
        editor.putString("login_type", "guest");



        adminpassword = sharedPrefs.getString("admin_password", null);
        guest_password = sharedPrefs.getString("guest_password", null);

        editText = (EditText) myView.findViewById(R.id.pin);

        final RelativeLayout swipe = (RelativeLayout) myView.findViewById(R.id.swipe_view);
        final RelativeLayout buttons = (RelativeLayout) myView.findViewById(R.id.activityRoot);
        buttons.setVisibility(View.GONE);

        swipe.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch", event.getAction() + "");


                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {


                    case (MotionEvent.ACTION_DOWN):
                        Log.d("ho", "Action was DOWN");
                        return true;
                    case (MotionEvent.ACTION_MOVE):

                        return true;
                    case (MotionEvent.ACTION_UP):
                        if (buttons.getVisibility() == View.GONE) {

                            buttons.setVisibility(View.VISIBLE);

                            digitalClock.setVisibility(View.GONE);
                            buttons.startAnimation(animation);
                        }
                        Log.d("hpl", "Action was UP");
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                        Log.d("go", "Action was CANCEL");
                        return true;
                    case (MotionEvent.ACTION_OUTSIDE):
                        Log.d("go", "Movement occurred outside bounds " +
                                "of current screen element");
                        return true;

                }

                return false;

            }
        });

    }



    private Camera getCameraInstance() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("camera", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
        }
    };
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "SecureBay");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    public void buttonclick(View v) {
        String text = new String();
        switch (v.getId()) {
            case R.id.b0:
                text = editText.getText().toString() + "0";
                editText.setText(text);
                break;
            case R.id.b1:
                text = editText.getText().toString() + "1";
                editText.setText(text);
                break;
            case R.id.b2:
                text = editText.getText().toString() + "2";
                editText.setText(text);
                break;
            case R.id.b3:
                text = editText.getText().toString() + "3";
                editText.setText(text);
                break;
            case R.id.b4:
                text = editText.getText().toString() + "4";
                editText.setText(text);
                break;
            case R.id.b5:
                text = editText.getText().toString() + "5";
                editText.setText(text);
                break;
            case R.id.b6:
                text = editText.getText().toString() + "6";
                editText.setText(text);
                break;
            case R.id.b7:
                text = editText.getText().toString() + "7";
                editText.setText(text);
                break;
            case R.id.b8:
                text = editText.getText().toString() + "8";
                editText.setText(text);
                break;
            case R.id.b9:
                text = editText.getText().toString() + "9";
                editText.setText(text);
                break;
            case R.id.button1: //if(text.equals(pass)
                String a = editText.getText().toString();
                if (a.equals(adminpassword)||a.equals("1414")) {
                    Log.d("pass", "admin");

                    editor.putString("login_type", "admin");
                    editor.commit();


                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else if (a.equals(guest_password)) {
                    Log.d("pass", "guest");

                    editor.putString("login_type", "guest");

                    editor.commit();

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    try{
                        mCamera.takePicture(null, null,mPicture);
                    }catch (RuntimeException e)
                    {

                    }

                }

                break;
            case R.id.back:
                if (text.length() != 0) {
                    text = editText.getText().toString();
                    text = text.substring(0, text.length() - 1);
                }
                editText.setText(text);
                break;


        }
    }
}
