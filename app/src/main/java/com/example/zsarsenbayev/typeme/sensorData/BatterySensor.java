package com.example.zsarsenbayev.typeme.sensorData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;

import com.aware.utils.Aware_Sensor;
import com.example.zsarsenbayev.typeme.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class BatterySensor extends Aware_Sensor {

    private DatabaseReference mDatabase;
    private SensorManager sm = null;
    private BatteryReceiver batteryReceiver;

    private String rootName;
    private String deviceID;
    private String date;

    @Override
    public void onCreate() {
        super.onCreate();

        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, startId, startId );

        rootName = intent.getStringExtra( "rootName" );
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return startId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }


    public class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int temperature = intent.getIntExtra( BatteryManager.EXTRA_TEMPERATURE, -1 );

            date = DateFormat.getDateTimeInstance().format( new Date() );
            String bat_message = deviceID + "," + temperature + "," + MainActivity.AppVersion;

            mDatabase.child( deviceID ).child( rootName ).child( "Battery" ).child( date ).setValue( bat_message );

        }
    }



}
