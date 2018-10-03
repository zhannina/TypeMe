package com.example.zsarsenbayev.typeme.sensorData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.aware.utils.Aware_Sensor;
import com.example.zsarsenbayev.typeme.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import github.nisrulz.easydeviceinfo.base.EasyNetworkMod;
import github.nisrulz.easydeviceinfo.base.NetworkType;

public class NetworkSensor extends Aware_Sensor {

    private String rootName;
    private DatabaseReference mDatabase;
    private String deviceID;
    private String date;

    private String networkState;
    private String networkType;


    @Override
    public void onCreate() {
        super.onCreate();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences( MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        deviceID = prefs.getString("device_id", "");

    }

    public void getNetworkInformation(){

        EasyNetworkMod easyNetworkMod = new EasyNetworkMod(this);

        boolean networkState1 = easyNetworkMod.isNetworkAvailable();

        if(networkState1 == true){
            networkState = "YES";
        }
        else{
            networkState = "NO";
        }

        int networkType1 = easyNetworkMod.getNetworkType();

        switch (networkType1) {
            case NetworkType.CELLULAR_UNKNOWN:
                networkType = "Unknown network type";
                break;
            case NetworkType.CELLULAR_UNIDENTIFIED_GEN:
                networkType = "Unidentified network generation";
                break;
            case NetworkType.CELLULAR_2G:
                networkType = "2G";
                break;
            case NetworkType.CELLULAR_3G:
                networkType = "3G";
                break;
            case NetworkType.CELLULAR_4G:
                networkType = "4G";
                break;
            case NetworkType.WIFI_WIFIMAX:
               networkType = "WIFI";
                break;
            case NetworkType.UNKNOWN:
            default:
                networkType = "Unknown network type";
                break;
        }

        String a_message = deviceID + "," + networkState + "," + networkType + "," + MainActivity.AppVersion;

        date = DateFormat.getDateTimeInstance().format(new Date());

        mDatabase.child(deviceID).child(rootName).child( "Network" ).child(date).setValue(a_message);
    }

    // get the root name from intent
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand( intent, flags, startId );

        rootName = intent.getStringExtra( "rootName" );
        getNetworkInformation();
        return startId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
