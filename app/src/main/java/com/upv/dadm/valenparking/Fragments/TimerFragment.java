package com.upv.dadm.valenparking.Fragments;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.upv.dadm.valenparking.MainActivity;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.Services.AlarmReceiver;

import java.util.Calendar;

import static android.Manifest.permission_group.CALENDAR;
import static android.content.Context.ALARM_SERVICE;

public class TimerFragment extends Fragment {

    View view;

    private static final String TAG = "TIMER";

    private TimePicker picker;
    private Button button;

    private int hour;
    private int minute;

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
                hour = picker.getCurrentHour();
                minute = picker.getCurrentMinute();

                disableChildren();
            }
        });

        return view;
    }


    public void disableChildren() {

        // poner el tiempo a una hora determinada
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent i = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pemdingIntent = PendingIntent.getBroadcast(getActivity(),1,i,0);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() /1000, pemdingIntent);

        Toast.makeText(getActivity(),"Notificación programada a las " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        // con el método cancel de alarmManager puedo cancelar la notificación
        //am.cancel(pemdingIntent);
        Log.d(TAG, "AQUIIIIIIIIII");

    }
}
