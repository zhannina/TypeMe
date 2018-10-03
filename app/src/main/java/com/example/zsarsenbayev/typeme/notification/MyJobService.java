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

import com.example.zsarsenbayev.typeme.MainActivity;
import com.example.zsarsenbayev.typeme.R;

public class MyJobService extends JobService {

    private static final String TAG = "NotificationService";
    public boolean time_flag1 = false;
    public boolean time_flag2 = false;
    public boolean reschedule_flag = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent content = PendingIntent.getActivity(this, 4567, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Calendar calendar = Calendar.getInstance();

        //notificationPublisher(content, manager);
        time_flag2 = time_info(calendar);
        time_flag2 = true;

        firstNotification(jobParameters, manager, content);

        if(time_flag2 == true){
            startThread(jobParameters, manager);
            if(reschedule_flag == true){
                reschedule(jobParameters, manager, content);
            }
        }


        return true;
    }

    public boolean time_info(final Calendar calendar){
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                if (currentHour > 9 && currentHour < 21){
                    time_flag1 = true;
                }
                else{
                    time_flag1 = false;
                }
            }

        }, 0, 60*1000);

        return time_flag1;
    }

    private void firstNotification(final JobParameters jobParameters,final NotificationManager manager, final PendingIntent content) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                notificationPublisher(content,manager);
            }
        }, 60*60*1000);

        reschedule_flag  = false;
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


    //use "volatile" to make sure the Thread is visible to other thread
    private void startThread(final JobParameters jobParameters,final NotificationManager manager){

        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                manager.cancel(4567);

            }
        }, 20*60*1000, 20*60*1000);


        reschedule_flag = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                jobFinished(jobParameters, false);
            }
        }).start();
    }

    private void reschedule(final JobParameters jobParameters,final NotificationManager manager, final PendingIntent content) {

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                manager.cancel(4567);
                notificationPublisher(content, manager);
            }

        }, 30*60*1000, 30*60*1000);

        reschedule_flag  = false;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return true;
    }
}
