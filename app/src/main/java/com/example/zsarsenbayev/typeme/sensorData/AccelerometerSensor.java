package com.example.zsarsenbayev.typeme.sensorData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.utils.Aware_Sensor;
import com.example.zsarsenbayev.typeme.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class AccelerometerSensor extends Aware_Sensor implements SensorEventListener {

    private DatabaseReference mDatabase;
    private SensorManager sm = null;
    private Sensor mAccelerometer;

    private String rootName;
    private String deviceID;
    private String date;

    @Override
    public void onCreate() {
        super.onCreate();

        // start AccelerometerSensor sensor
        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, true);
        //Set sampling frequency
        Aware.setSetting(this, Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
        //Apply settings
        Aware.startSensor(this, Aware_Preferences.STATUS_LINEAR_ACCELEROMETER);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float accX = event.values[0];
        float accY = event.values[1];
        float accZ = event.values[2];

        //String ts = String.valueOf(System.currentTimeMillis());
        date = DateFormat.getDateTimeInstance().format(new Date());

        String acc_message = deviceID + "," + accX + "," + accY + "," + accZ + "," + MainActivity.AppVersion;

        mDatabase.child(deviceID).child(rootName).child("Accelerometer").child(date).setValue(acc_message);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        rootName = intent.getStringExtra( "rootName" );
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        return startId;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop AccelerometerSensor sensor
        Aware.setSetting(this, Aware_Preferences.STATUS_ACCELEROMETER, false);
        sm.unregisterListener(this, mAccelerometer);
    }

}
