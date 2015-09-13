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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pw.victor.parkingcronsample.R;
import com.pw.victor.parkingcronsample.activities.TimeOption;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ButtonsFragment  extends Fragment implements  View.OnClickListener{

    private Context ctx;

    private FragmentManager fm;

    private SharedPreferences sharedPrefs;

    private  SharedPreferences sharedPrefsNotification;

    RelativeLayout rel;

    private PendingIntent pendingIntent;

    private PendingIntent pendingIntentAlarmDone;

    public ButtonsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_buttons, container,false);

        ctx = container.getContext();

        fm = getFragmentManager();

        sharedPrefs = this.getActivity().getSharedPreferences(TimeOption.PREFERENCE_NAME, Context.MODE_PRIVATE);

        sharedPrefsNotification = getDefaultSharedPreferences(ctx);

        rel = (RelativeLayout) view.findViewById(R.id.layoutButtons);

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent("EXECUTE_ALARM");
        Intent alarmIntentDone = new Intent("EXECUTE_ALARM");

        Bundle mBundle = new Bundle();

        mBundle.putBoolean("isDone", true);

        alarmIntentDone.putExtras(mBundle);

        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, alarmIntent, 0);

        pendingIntentAlarmDone = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 1, alarmIntentDone, 0);

        initButtons();

        JodaTimeAndroid.init(view.getContext());

        return view;
    }

    public static ButtonsFragment newInstance() {
        ButtonsFragment myFragment = new ButtonsFragment();

        return myFragment;
    }

    @Override
    public void onClick(View view) {

        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putString(TimeOption.TIME_CHOSEN, view.getTag().toString());

        editor.putString(TimeOption.TIME_STARTED, DateTimeFormat.forPattern("HH:mm:ss").print(System.currentTimeMillis()));

        editor.commit();

        DateTime date = new DateTime();

        date = date.plusMillis(DateTimeFormat.forPattern("HH:mm:ss")
                .parseLocalTime(view.getTag().toString()).getMillisOfDay());

       //date = date.minusMinutes(1);

        start(date);

        initFragment();

    }

    private void initFragment(){

        if(sharedPrefs.getString(TimeOption.TIME_CHOSEN,null) != null){

            FragmentTransaction ft = fm.beginTransaction();

            PainelActiveFragment paFt = new PainelActiveFragment();

            ft.replace(R.id.dynFrm, paFt);

            ft.commit();
        }

    }

    public void start(DateTime date) {

        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        manager.set(AlarmManager.RTC_WAKEUP, date.minusMinutes(Integer.parseInt(sharedPrefsNotification.getString("prefAlarm","0"))).getMillis(), pendingIntent);

        manager.set(AlarmManager.RTC_WAKEUP, date.getMillis(), pendingIntentAlarmDone);

        Toast.makeText(ctx, "Alarm Set", Toast.LENGTH_SHORT).show();

    }

    private void initButtons(){

        for(int i=0;i<rel.getChildCount();i++){
            View child=rel.getChildAt(i);
            child.setOnClickListener(this);
        }

    }
}
