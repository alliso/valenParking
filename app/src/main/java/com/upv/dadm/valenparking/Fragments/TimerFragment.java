package com.upv.dadm.valenparking.Fragments;

import android.app.AlarmManager;
import android.support.v4.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    private TextView text;

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
                disableChildren();
                if(checkTimer() <= 0)
                    Toast.makeText(getContext(),"Hora no valida", Toast.LENGTH_LONG).show();
                else {
                    startTimer(checkTimer());
                    changeUI();
                }
            }
        });
        text = view.findViewById(R.id.timerText);



        return view;
    }

    private int checkTimer() {
        hour = picker.getCurrentHour();
        minute = picker.getCurrentMinute();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int milis = hour * 60 * 60 * 1000 + minute * 60 * 1000;
        int currentMilis = currentHour * 60 * 60 * 1000 + currentMinute * 60 * 1000;

        return milis - currentMilis;
    }

    public void startTimer(int diff){
            new CountDownTimer(diff, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60 ;
                    int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
                    int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);

                    String time = "";
                    if(hours > 0)
                        time = hours < 10 ? "0" + hours + ":" : hours + ":";
                    if(minutes > 0)
                        time += minutes < 10 ? "0" + minutes + ":" : minutes + ":";
                    if(seconds > 0)
                        time += seconds < 10 ? "0" + seconds : seconds;

                    text.setText(time);
                }

                @Override
                public void onFinish() {

                }
            }.start();
    }


    public void disableChildren() {

        // poner el tiempo a una hora determinada
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent i = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent pemdingIntent = PendingIntent.getBroadcast(getActivity(),11111,i,0);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        long updateInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        am.setRepeating(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis() - AlarmManager.INTERVAL_HALF_HOUR) /1000, updateInterval, pemdingIntent);

        Toast.makeText(getActivity(),"Se enviará una notificación media hora antes.", Toast.LENGTH_SHORT).show();
        // con el método cancel de alarmManager puedo cancelar la notificación
        //am.cancel(pemdingIntent);
        Log.d(TAG, "AQUIIIIIIIIII");

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
