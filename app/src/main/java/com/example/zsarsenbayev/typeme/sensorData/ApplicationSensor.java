package com.example.zsarsenbayev.typeme.sensorData;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Sensor;
import com.example.zsarsenbayev.typeme.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import github.nisrulz.easydeviceinfo.base.EasyAppMod;

public class ApplicationSensor extends Aware_Sensor {

    private String rootName = "Application";
    private String applicationHeader = "recordID,deviceID,PackageName,ApplicationName,IsSystemApp,AppVersion";
    private DatabaseReference mDatabase;
    private String deviceID;
    private String date;

    ContextReceiver contextReceiver;


    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        setHeader( prefs );

        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
        //getApplicationInfomation();

        IntentFilter contextFilter = new IntentFilter();
        contextFilter.addAction( Applications.ACTION_AWARE_APPLICATIONS_FOREGROUND);
        contextReceiver = new ContextReceiver();
        registerReceiver(contextReceiver, contextFilter);

    }


    public void setHeader(SharedPreferences prefs){

        if(prefs.getBoolean("FirstApplicationService", true)){
            mDatabase.child(deviceID).child(rootName).child("Date").setValue(applicationHeader);
            prefs.edit().putBoolean("FirstApplicationService", false);
            prefs.edit().commit();
        }
    }

    public void getApplicationInfomation(){

        EasyAppMod easyAppMod = new EasyAppMod(this);

        String activityName = easyAppMod.getActivityName();
        String packageName = easyAppMod.getPackageName();
        String appName = easyAppMod.getAppName();
        String a_message = activityName + "," + packageName + "," + appName + MainActivity.AppVersion;

        String date = DateFormat.getDateTimeInstance().format(new Date());

        mDatabase.child(deviceID).child(rootName).child(date).setValue(a_message);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, false);
        unregisterReceiver( contextReceiver );
        super.onDestroy();
    }

    public static class ContextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        //Action for context here.

            String packageName = intent.getStringExtra( "package_name" );

            //Toast.makeText(context,packageName , Toast.LENGTH_SHORT).show();

            System.out.print( packageName );


        }
    }

}
