package com.example.zsarsenbayev.typeme.sensorData;


import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Sensor;
import com.example.zsarsenbayev.typeme.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.text.DateFormat;
import java.util.Date;

public class LightSensor extends Aware_Sensor implements SensorEventListener {

    private DatabaseReference mDatabase;
    private SensorManager sm = null;
    private Sensor mLight;

    private String rootName;
    private String deviceID;


    @Override
    public void onCreate() {
        super.onCreate();

        // start AccelerometerSensor sensor
        Aware.setSetting(this, Aware_Preferences.STATUS_LIGHT, true);
        //Set sampling frequency
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_LIGHT, 200000);
        //Apply settings
        Aware.startSensor(this, Aware_Preferences.STATUS_LIGHT);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float light_data = event.values[0];
        String lighLuminance = String.valueOf( light_data );

        String date = DateFormat.getDateTimeInstance().format(new Date());
        String light_message = deviceID + ", " + lighLuminance + ", " + MainActivity.AppVersion;

        mDatabase.child(deviceID).child(rootName).child("Light").child(date).setValue(light_message);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        rootName = intent.getStringExtra( "rootName" );
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sm = (SensorManager) getSystemService( Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            mLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
            sm.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return startId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop AccelerometerSensor sensor
        Aware.setSetting(this, Aware_Preferences.STATUS_LIGHT, false);
        sm.unregisterListener(this, mLight);
    }
}
