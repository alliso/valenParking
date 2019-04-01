package com.upv.dadm.valenparking.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.upv.dadm.valenparking.R;

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "default")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());

        Log.d(TAG, "AQUIIIIIIIIII");

    }
}
