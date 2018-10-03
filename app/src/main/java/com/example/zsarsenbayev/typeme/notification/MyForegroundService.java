package com.example.zsarsenbayev.typeme.notification;

import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zsarsenbayev.typeme.R;


public class MyForegroundService extends Service {

    public static final String log_tag = "MyForegroundService";
    private static final String TAG = "MyNotificationService";
    static boolean active = false;

    @Override
    public void onCreate(){

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon( R.drawable.foreground_pic)
                .setContentTitle("The foreground service is running at the background")
                .setContentText("Please do not click on this notice").build();
                //.setContentIntent(pendingIntent).build();

        startForeground(1234, notification);

        if(active == true){

        }else{
             scheduleJob();
        }

        //startForeground(100, notification);
    }

    public void scheduleJob(){

        ComponentName componentName = new ComponentName(this,MyJobService.class);
        JobInfo info = new JobInfo.Builder(666, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setRequiresDeviceIdle(false)
                .setPeriodic(24*60*60*1000)
                //.setExecutionWindow()
                .setPersisted(true)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);

        if(resultCode == JobScheduler.RESULT_SUCCESS){
            active = true;
            Log.d(TAG, "Job scheduled");
        }else{
            Log.d(TAG, "Failed to schedule job");
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(log_tag, "In onDestroy");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
