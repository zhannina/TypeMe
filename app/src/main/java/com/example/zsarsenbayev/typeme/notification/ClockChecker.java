package com.example.zsarsenbayev.typeme.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.R;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ClockChecker extends JobService {

    private static final String TAG = "ClockChecker Service";
    private Calendar calendar;
    private PendingIntent content;
    NotificationManager manager;

    @Override
    public boolean onStartJob(JobParameters params) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        content = PendingIntent.getActivity(this, 4567, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        delayedMessage();

        return true;

    }


    public void delayedMessage(){

        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startDelayedJob();
            }
        }, 60*60*1000);
    }

    public void startDelayedJob(){
        calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        //We send the notification and start notification service only between 9:00am to 9:00pm every day.
        if (currentHour > 9 && currentHour < 21){
            notificationPublisher( content, manager );
            scheduleClockChecker();
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService( Context.JOB_SCHEDULER_SERVICE );
            jobScheduler.cancel(12085);
        }
    }

    //Send the notification with related content
    public void notificationPublisher(PendingIntent content, NotificationManager manager){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Survey Time!")
                .setContentText("Please help us finish the activities")
                .setContentIntent(content)
                .setSmallIcon( R.drawable.notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify(4567,builder.build());
    }

    //Start the "MyJobService" class here
    public void scheduleClockChecker(){
        ComponentName componentName = new ComponentName(this, MyJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(12086, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();

        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "MyJob Service scheduled!");
        } else {
            Log.d(TAG, "MyJob Service not scheduled");
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
