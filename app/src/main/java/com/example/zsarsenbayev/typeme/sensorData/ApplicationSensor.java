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
    private String applicationHeader = "deviceID,PackageName,AppVersion";
    private DatabaseReference mDatabase;
    private String deviceID;
    private String packageName;

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        setHeader( prefs );

        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, true);
    }


    //If the Application sensor is the first time to start, set the head in Firebase
    public void setHeader(SharedPreferences prefs){
        if(prefs.getBoolean("FirstApplicationService", true)){
            mDatabase.child(deviceID).child(rootName).child("Date").setValue(applicationHeader);
            prefs.edit().putBoolean("FirstApplicationService", false);
            prefs.edit().commit();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        packageName = intent.getStringExtra( "packageName" );
        String a_message = deviceID +"," + packageName + "," + MainActivity.AppVersion;
        String date = DateFormat.getDateTimeInstance().format(new Date());
        mDatabase.child(deviceID).child(rootName).child(date).setValue(a_message);

        stopSelf();
        return startId;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Aware.setSetting(this, Aware_Preferences.STATUS_APPLICATIONS, false);
        super.onDestroy();
    }


}
