package com.example.zsarsenbayev.typeme;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by zsarsenbayev on 11/20/17.
 */

public class TypingSensorsService extends Service implements SensorEventListener {

    private final String ACCELEROMETER_HEADER = "TimeStamp,Date,Acceleration_X,Acceleration_Y,Acceleration_Z\n";
    private final String BATTERY_HEADER = "TimeStamp,Date,BatTemp\n";

    private SensorManager sm = null;
    private Sensor mAccelerometer;

    private BufferedWriter bufferedWriter;
    private StringBuilder stringBuilder;
    private File accFile;
    private File batFile;

    private BatteryReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                MyView.WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            Log.e("MYDEBUG", "Failed to create directory: " + MyView.WORKING_DIRECTORY);
            Toast.makeText(this, "Couldn't create directory", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }

        batteryReceiver = new BatteryReceiver();

        SharedPreferences prefs = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);



        String participantCode = prefs.getString("participantCode", "");
        String genderCode = prefs.getString("genderCode", "");
        String conditionCode = prefs.getString("conditionCode", "");

        String base = "TYPIING-" + participantCode + "-" +
                genderCode + "-" + conditionCode;

        accFile = new File(dataDirectory, base+"-accelerometer.csv");
        batFile = new File(dataDirectory, base+"-battery.csv");

        if(!accFile.exists())
        {
            try {

                bufferedWriter = new BufferedWriter(new FileWriter(accFile, true));
                bufferedWriter.append(ACCELEROMETER_HEADER, 0, ACCELEROMETER_HEADER.length());
                bufferedWriter.flush();

            } catch (IOException e) {
                Log.i("MYDEBUG", "Error writing data files (acc)! Exception: " + e.toString());
                System.exit(0);
            }
        }

        if(!batFile.exists())
        {
            try {

                bufferedWriter = new BufferedWriter(new FileWriter(batFile, true));
                bufferedWriter.append(BATTERY_HEADER, 0, BATTERY_HEADER.length());
                bufferedWriter.flush();

            } catch (IOException e) {
                Log.i("MYDEBUG", "Error writing data files (bat)! Exception: " + e.toString());
                System.exit(0);
            }
        }

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float accX = event.values[0];
        float accY = event.values[1];
        float accZ = event.values[2];

//        Log.d("TAG","ACC: " + accX + " - " + accY + " - " + accZ);

        String ts = String.valueOf(System.currentTimeMillis());

        String date = DateFormat.getDateTimeInstance().format(new Date());

        stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s,%s,%f,%f,%f\n", ts, date, accX, accY, accZ));
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(accFile, true));
            bufferedWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
            bufferedWriter.flush();
        } catch (IOException e) {
            Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES (acc): e = " + e);
        }
        stringBuilder.delete(0, stringBuilder.length());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(this);
        unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }

    public class BatteryReceiver extends BroadcastReceiver {

        private StringBuilder batStringBuilder;
        private BufferedWriter batBufferedWriter;
        @Override
        public void onReceive(Context context, Intent intent) {
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

//            Log.d("TAG","TEMP: " + temperature);

            String ts = String.valueOf(System.currentTimeMillis());

            String date = DateFormat.getDateTimeInstance().format(new Date());

            batStringBuilder = new StringBuilder();
            batStringBuilder.append(String.format("%s,%s,%d\n", ts, date, temperature));
            try {

                batBufferedWriter = new BufferedWriter(new FileWriter(batFile, true));
                batBufferedWriter.write(batStringBuilder.toString(), 0, batStringBuilder.length());
                batBufferedWriter.flush();
            } catch (IOException e) {
                Log.e("MYDEBUG", "ERROR WRITING TO DATA FILES (bat): e = " + e);
            }
            batStringBuilder.delete(0, batStringBuilder.length());

        }
    }

}
