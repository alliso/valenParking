package com.upv.dadm.valenparking.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.upv.dadm.valenparking.MainActivity;
import com.upv.dadm.valenparking.R;

import java.text.DateFormat;
import java.util.Calendar;

public class TimerFragment extends Fragment {

    View view;

    private static final String TAG = "TIMER";

    private TimePicker picker;
    private Button button;
    private TextView text;

    private int hour;
    private int minute;
    private CountDownTimer countDown;

    public TimerFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timer, null);

        picker = view.findViewById(R.id.timerPicker);
        button = view.findViewById(R.id.timerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableChildren();
                changeUI();
                startTimer();
            }
        });
        text = view.findViewById(R.id.timerText);



        return view;
    }

    public void startTimer(){
        hour = picker.getCurrentHour();
        minute = picker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int milis = hour * 60 * 60 * 1000 + minute * 60 * 1000;
        int currentMilis = currentHour * 60 * 60 * 1000 + currentMinute * 60 * 1000;

        int diff = milis - currentMilis;

        new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
                text.setText(hours + ":" + minutes);
            }

            @Override
            public void onFinish() {

            }
        }.start();


    }


    public void disableChildren() {

        Intent i = new Intent(getActivity(), MainActivity.class);
        PendingIntent pemdingIntent = PendingIntent.getBroadcast(getActivity(),0,i,0);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , 8000, pemdingIntent);



       /* NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
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

        /*Intent i = new Intent(getActivity(), MainActivity.class);
        PendingIntent pemdingIntent = PendingIntent.getActivity(getActivity(),0,i,0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setContentIntent(pemdingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Hearty365")
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("PARKING NOTIFICATION")
                .setContentText("Te notifico locooooo.")
                .setContentInfo("Info");

        manager.notify(1, notificationBuilder.build());*/
    }

    public void changeUI(){
        if(picker.getVisibility() == View.INVISIBLE) {
            picker.setVisibility(View.VISIBLE);
            text.setVisibility(View.INVISIBLE);
        } else {
            picker.setVisibility(View.INVISIBLE);
            text.setVisibility(View.VISIBLE);
        }
    }
}
