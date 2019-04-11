package com.upv.dadm.valenparking.Services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelNotification extends BroadcastReceiver {

    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        id = intent.getIntExtra("notification_id", 1);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}

