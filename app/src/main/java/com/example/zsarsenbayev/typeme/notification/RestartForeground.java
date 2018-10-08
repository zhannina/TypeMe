package com.example.zsarsenbayev.typeme.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartForeground extends BroadcastReceiver {

    // Once the mobile phone is rebooted completely, restart the foreground service
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MyForegroundService.class);
        context.startService(service);
    }
}
