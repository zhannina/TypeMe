package com.example.zsarsenbayev.typeme.circleActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.findIconActivity.FindIconActivity;
import com.example.zsarsenbayev.typeme.sensorData.AccelerometerSensor;
import com.example.zsarsenbayev.typeme.sensorData.BatterySensor;
import com.example.zsarsenbayev.typeme.sensorData.LightSensor;
import com.example.zsarsenbayev.typeme.sensorData.NetworkSensor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CirclesActivity extends AppCompatActivity {

    SharedPreferences prefs;
    public static final String MYPREFS = "MyPrefs";

    private String rootName = "Circle Activity";
    public static Intent accelerometerIntent;
    public static Intent batteryIntent;
    public static Intent lightIntent;
    public static Intent networkIntent;

    private final String ACCELEROMETER_HEADER = "DeviceID,Acceleration_X,Acceleration_Y,Acceleration_Z,AppVersion";
    private final String BATTERY_HEADER = "DeviceID,BatteryTemp,AppVersion";
    private final String LIGHT_HEADER = "DeviceID,LightLuminance,AppVersion";
    private final String NETWORK_HEADER = "deviceID,NetworkState,NetworkType,AppVersion";

    String deviceID;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));

        prefs = getApplicationContext().getSharedPreferences(MYPREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFS, MODE_PRIVATE).edit();


        fileHeader();
        accelerometerIntent = new Intent(CirclesActivity.this, AccelerometerSensor.class);
        accelerometerIntent.putExtra( "rootName", rootName );

        batteryIntent = new Intent(CirclesActivity.this, BatterySensor.class);
        batteryIntent.putExtra( "rootName", rootName );

        lightIntent = new Intent( CirclesActivity.this, LightSensor.class );
        lightIntent.putExtra( "rootName", rootName );

        networkIntent = new Intent( CirclesActivity.this, NetworkSensor.class );
        networkIntent.putExtra( "rootName", rootName );

        startService(accelerometerIntent);
        startService(batteryIntent);
        startService(lightIntent);
        startService(networkIntent);

    }

    public void fileHeader(){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

        if(prefs.getBoolean("FirstCircleAccelerometerService", true)){
            mDatabase.child(deviceID).child(rootName).child("Accelerometer").child("Date").setValue(ACCELEROMETER_HEADER);
            prefs.edit().putBoolean("FirstCircleAccelerometerService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstCircleBatteryService", true)){
            mDatabase.child(deviceID).child(rootName).child("Battery").child("Date").setValue(BATTERY_HEADER);
            prefs.edit().putBoolean("FirstCircleBatteryService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstCircleLightService", true)){
            mDatabase.child(deviceID).child(rootName).child("Light").child("Date").setValue(LIGHT_HEADER);
            prefs.edit().putBoolean("FirstCircleLightService", false);
            prefs.edit().commit();
        }

        if(prefs.getBoolean("FirstCircleNetworkService", true)){
            mDatabase.child(deviceID).child(rootName).child( "Network" ).child("Date").setValue(NETWORK_HEADER);
            prefs.edit().putBoolean("FirstCircleNetworkService", false);
            prefs.edit().commit();
        }

    }

        public void stopServices(){
        stopService(accelerometerIntent);
        stopService(batteryIntent);
        stopService(lightIntent);
        stopService( networkIntent );
    }


    @Override
    protected void onResume(){
        super.onResume();
        getDisplayContentSize();
    }

    public void getDisplayContentSize(){
        float result;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        result = getResources().getDimensionPixelSize(resourceId);

        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        float maxX = mdispSize.x;
        float maxY = mdispSize.y-result;
        prefs = getApplicationContext().getSharedPreferences(MYPREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFS, MODE_PRIVATE).edit();
        editor.putFloat("maxX", maxX);
        editor.putFloat("maxY", maxY);

        editor.commit();
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
