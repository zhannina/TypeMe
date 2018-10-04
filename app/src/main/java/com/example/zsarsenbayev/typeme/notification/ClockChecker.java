package com.example.zsarsenbayev.typeme.notification;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class ClockChecker extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
