package com.upv.dadm.valenparking.Services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.content.Context.ALARM_SERVICE;

public class CancelNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase("CANCEL")) {

            //elimina la notificación
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            //cancelo las siguientes notificaciones.
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pemdingIntent = PendingIntent.getBroadcast(context,11111,i,0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pemdingIntent);
        }
    }
}

