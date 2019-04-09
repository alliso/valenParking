package com.upv.dadm.valenparking.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.upv.dadm.valenparking.MainActivity;
import com.upv.dadm.valenparking.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context){


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
        }

        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("key", R.id.main_menu_timer);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pemdingIntent = PendingIntent.getActivity(context,0,i,0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setContentIntent(pemdingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Hearty365")
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("PARKING NOTIFICATION")
                .setContentText("Te quedan 5 minutos de parking.")
                .setContentInfo("Info");

        manager.notify(1, notificationBuilder.build());
    }
}
