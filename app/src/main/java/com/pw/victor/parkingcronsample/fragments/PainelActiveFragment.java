package com.pw.victor.parkingcronsample.fragments;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pw.victor.parkingcronsample.R;
import com.pw.victor.parkingcronsample.activities.TimeOption;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class PainelActiveFragment extends Fragment implements  View.OnClickListener{


    private Button btnReset;

    private Context ctx;

    private SharedPreferences sharedPrefs;

    private FragmentManager fm;

    private TextView time;

    private PendingIntent pendingIntent;

    private PendingIntent pendingIntentAlarmDone;

    private CountDownTimer countDown;

    private TextView txtAlarmDone;

    private TextView txtAlarmPre;

    private TextView txtTimeSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent("EXECUTE_ALARM");
        Intent alarmIntentDone = new Intent("EXECUTE_ALARM");

        Bundle mBundle = new Bundle();

        mBundle.putBoolean("isDone", true);

        alarmIntentDone.putExtras(mBundle);

        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, alarmIntent, 0);

        pendingIntentAlarmDone = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 1, alarmIntentDone, 0);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_painel_active, container, false);

        ctx = container.getContext();

        sharedPrefs = this.getActivity().getSharedPreferences(TimeOption.PREFERENCE_NAME, Context.MODE_PRIVATE);

        btnReset = (Button) view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        fm = getFragmentManager();

        JodaTimeAndroid.init(view.getContext());

        time = (TextView) view.findViewById(R.id.timeRemaining);

        txtAlarmDone = (TextView) view.findViewById(R.id.textAlarmDone);

        txtAlarmPre = (TextView) view.findViewById(R.id.textAlarmPre);

        txtTimeSelected = (TextView) getActivity().findViewById(R.id.txtTimeSelected);

        txtTimeSelected.setText(" em " + TimeOption.getCodeTimeChosen(sharedPrefs.getString(TimeOption.TIME_CHOSEN, "00:00:00")));

        return view;
    }

    @Override
    public void onStart() {
        initCountDown();
        super.onStart();
    }

    public void initCountDown(){

        LocalTime timeCurr = new LocalTime();

        LocalTime timeChosen;

        if(sharedPrefs.getString(TimeOption.TIME_STARTED,null) == null) {
            try {
                timeChosen = new LocalTime(sharedPrefs.getString(TimeOption.TIME_CHOSEN, "00:00:00"));
            }catch (Exception ex){
                timeChosen = new LocalTime("00:00:00");
            }
        }else{

            int valueDiff = new LocalTime().getMillisOfDay() - new LocalTime(sharedPrefs.getString(TimeOption.TIME_STARTED,"00:00:00")).getMillisOfDay();

            int valueChosenInMilis = new LocalTime(sharedPrefs.getString(TimeOption.TIME_CHOSEN,"00:00:00")).getMillisOfDay();

            if(valueChosenInMilis > valueDiff) {
                valueDiff = valueChosenInMilis - valueDiff;
            }else{
                valueDiff = 0;
            }

            timeChosen = LocalTime.fromMillisOfDay(valueDiff);
        }

        int period = (timeCurr.getMillisOfDay() + timeChosen.getMillisOfDay());

        period -= timeCurr.getMillisOfDay();

        if(period > 0){
            LocalTime time = new LocalTime().plusMillis(period);
            txtAlarmDone.setText(time.toString());
            txtAlarmPre.setText(time.minusMinutes(1).toString());
            /**
             * txtAlarmDone.setText("Alarm 1" + DateTimeFormat.forPattern("HH:mm:ss").parseLocalTime(time.toString()).toString());
             txtAlarmPre.setText("Alarm 2" + DateTimeFormat.forPattern(time.minusMinutes(1).toString()).toString());
             */

        }

        startCountDown(period);

    }

    @Override
    public void onClick(View view) {

        cancel();
        finalizeClock();
    }

    public void finalizeClock(){

        //cancel();

        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(sharedPrefs.contains(TimeOption.TIME_CHOSEN))
        editor.remove(TimeOption.TIME_CHOSEN);

        if(sharedPrefs.contains(TimeOption.TIME_STARTED))
        editor.remove(TimeOption.TIME_STARTED);

        editor.commit();

        String valor = sharedPrefs.getString(TimeOption.TIME_STARTED,null);

        initFragment();
    }

    private void initFragment(){

        if(sharedPrefs.getString(TimeOption.TIME_CHOSEN,null) == null){

            FragmentTransaction ft = fm.beginTransaction();

            ButtonsFragment btnFt = new ButtonsFragment();

            ft.replace(R.id.dynFrm, btnFt);

            ft.commit();
        }

    }


    public void startCountDown(int period){

        countDown = new CountDownTimer(period, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            DateTimeFormatter mdate = DateTimeFormat.forPattern("HH:mm:ss");


            public void onTick(long millisUntilFinished) {

                time.setText(mdate.print(new LocalTime().withMillisOfDay((int) millisUntilFinished)));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                finalizeClock();
            }


        }
                .start();
    }

    @Override
    public void onStop() {
        if (countDown != null) {
            countDown.cancel();
        }
        super.onDestroy();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        manager.cancel(pendingIntentAlarmDone);
        Toast.makeText(ctx, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

}
