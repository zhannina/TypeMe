package com.example.zsarsenbayev.typeme.notification;

import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zsarsenbayev.typeme.R;
import com.example.zsarsenbayev.typeme.WindowChangeDetectingService;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class MyForegroundService extends Service {

    public static final String log_tag = "MyForegroundService";
    private static final String TAG = "MyNotificationService";
    private Intent windowChangeService;

    @Override
    public void onCreate(){

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon( R.drawable.foreground_pic)
                .setContentTitle("The foreground service is running at the background")
                .setContentText("Please do not click on this notice").build();

        //start the foreground service with ID "1234"
        startForeground(1234, notification);

        windowChangeService = new Intent( this, WindowChangeDetectingService.class );
        startService( windowChangeService );

        //Start the clockChecker service every time the Foreground Service is restarted
        scheduleClockChecker();
    }

    // Start the ClockChecker service when the foreground service start
    public void scheduleClockChecker(){
        ComponentName componentName = new ComponentName(this, ClockChecker.class);
        JobInfo jobInfo = new JobInfo.Builder(12085, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();

        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Clock Checker Job scheduled!");
        } else {
            Log.d(TAG, "Clock Checker Job not scheduled");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService( windowChangeService );
        Log.d(log_tag, "In onDestroy");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null;
    }
}
