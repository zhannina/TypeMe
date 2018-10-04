package com.example.zsarsenbayev.typeme;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.provider.Settings.Secure;

import com.example.zsarsenbayev.typeme.circleActivity.CirclesActivity;
import com.example.zsarsenbayev.typeme.findIconActivity.FindIconActivity;
import com.example.zsarsenbayev.typeme.instructions.Circle_instruction;
import com.example.zsarsenbayev.typeme.instructions.FindIcon_instruction;
import com.example.zsarsenbayev.typeme.instructions.Typing_instruction;
import com.example.zsarsenbayev.typeme.notification.MyForegroundService;
import com.example.zsarsenbayev.typeme.sensorData.ApplicationSensor;
import com.example.zsarsenbayev.typeme.typingActivity.TypingTaskActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    public static ArrayList<Class<?>> activities, activity_instructions;

    private static final int PERMISSIONS_REQUEST = 12;
    private Button quitButton;
    private Button acceptButton;

    private String rootName = "Timezone";
    private String TIMEZONE_HEADER = "deviceID,timezone,AppVersion";
    private Button confirmQuitButton;

    public static int number_of_task = 0;
    String device_id;

    SharedPreferences sharedPrefs;
    DatabaseReference mDatabase;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String COMPLETED_ONBOARDING_PREF_NAME = "On Boarding Activity";
    public static final String AppVersion = "1.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_quit);
        activities = new ArrayList<Class<?>>();
        activity_instructions = new ArrayList<Class<?>>();

        activities.add(CirclesActivity.class);
        activities.add(FindIconActivity.class);
        activities.add(TypingTaskActivity.class);
        activities.add(QuestionnaireActivity.class);

        activity_instructions.add( Circle_instruction.class);
        activity_instructions.add( FindIcon_instruction.class);
        activity_instructions.add( Typing_instruction.class);

        sharedPrefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //editor.putInt("tasks of today", number_of_task);

        confirmQuitButton = (Button) findViewById(R.id.confirm_exit);

        confirmQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editor.putInt("tasks of today", number_of_task++);

                finish();

            }
        });

        if(sharedPrefs.getBoolean("firstStart", true)){
            // update sharedpreference - another start wont be the first

            setContentView(R.layout.activity_main);
            quitButton = (Button) findViewById(R.id.quit);
            acceptButton = (Button) findViewById(R.id.accept);
            requestPermissions();

            quitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);
                }
            });

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putBoolean("firstStart", false);
                    editor.commit();

                    if(!isMyServiceRunning(MyForegroundService.class)){
                        startForegroundService();
                    }

                    finish();

                    if(sharedPrefs.getBoolean(
                            COMPLETED_ONBOARDING_PREF_NAME, true)){

                        startOnBoardingActivity();
                    }

                }
            });
        }
        else{
            if(!isMyServiceRunning(MyForegroundService.class)){
                startForegroundService();
            }

            device_id = myDeviceID();
            editor.putString("device_id", device_id);
            editor.commit();

            getCurrentTimezone();
            requestPermissions();

            startMyActivity();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    requestPermissions();

                }
                return;
            }

        }
    }

    public void getCurrentTimezone(){

        Calendar calendar = Calendar.getInstance();

        TimeZone tz = TimeZone.getDefault();

        String temp = tz.getDefault().getID();
        String temp1 = calendar.getTimeZone().getDisplayName(false, TimeZone.SHORT);
        String timzone = temp + "-" + temp1;

        String date = DateFormat.getDateTimeInstance().format(new Date());

        if(sharedPrefs.getBoolean("FirstTimezone", true)){
            mDatabase.child(device_id).child(rootName).child("Date").setValue(TIMEZONE_HEADER);
            sharedPrefs.edit().putBoolean("FirstTimezone", false);
            sharedPrefs.edit().commit();
        }

        String timezone_message = device_id + "," + timzone + "," + AppVersion;
        mDatabase.child(device_id).child(rootName).child(date).setValue(timezone_message);

    }

    private void requestPermissions()
    {
        Log.d("TAG", "Whatever1");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Whatever2");

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST);
        }
        else {
            Log.d("TAG", "Whatever3");

        }
    }


    public void startOnBoardingActivity(){
        startActivity(new Intent(this, OnboardingActivity.class));
    }


    public String myDeviceID(){
        String android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    public void startForegroundService(){
        Intent i = new Intent(this, MyForegroundService.class);
        this.startService(i);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void startMyActivity() {

        int activitySize = activities.size() - 1;
        Intent questionnaireIntent = new Intent( MainActivity.this, activities.get(activitySize) );
        activities.remove(activitySize);
        questionnaireIntent.putExtra("activity", activities);
        questionnaireIntent.putExtra( "activity instruction", activity_instructions );
        startActivity( questionnaireIntent );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please do not press the back button", Toast.LENGTH_SHORT).show();
    }
}
