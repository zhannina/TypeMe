package com.example.zsarsenbayev.typeme.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.logging.Handler;
import android.os.Handler;
import android.widget.Toast;

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.R;

public class MyJobService extends JobService {

    private static final String TAG = "NotificationService";

    Timer cancelTimer, rescheduleTimer;
    NotificationManager manager;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent content = PendingIntent.getActivity(this, 4567, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //first cancel the possible existing notification
        startThread( jobParameters, manager );
        reschedule( jobParameters, manager, content );

        return true;
    }

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

    //cancel the existing notification every 20 minutes
    private void startThread(final JobParameters jobParameters,final NotificationManager manager){

        cancelTimer = new Timer();
        cancelTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                manager.cancel(4567);
            }
        }, 20*60*1000, 50*60*1000);

    }

    //If not stopped, keep rescheduling the notification every 30 minutes
    private void reschedule(final JobParameters jobParameters,final NotificationManager manager, final PendingIntent content) {

        rescheduleTimer = new Timer();
        rescheduleTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                manager.cancel(4567);
                notificationPublisher(content, manager);
            }

        }, 30*60*1000, 50*60*1000);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        //In case there remains a notification to cancel
        manager.cancel(4567);

        // stop the timers
        cancelTimer.cancel();
        cancelTimer.purge();
        rescheduleTimer.cancel();
        rescheduleTimer.purge();

        return true;
    }
}
