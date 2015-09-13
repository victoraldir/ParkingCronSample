package com.pw.victor.parkingcronsample.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pw.victor.parkingcronsample.R;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AlarmActivity extends Activity {

    private CountDownTimer countDown;
    private TextView timeRemaining;
    private SharedPreferences sharedPrefs;
    private  SharedPreferences sharedPrefsNotification;
    private Window wind;
    private PendingIntent pendingIntent;
    Ringtone ringtoneAlarm;
    static boolean active = false;
    Vibrator v;

    boolean isDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        JodaTimeAndroid.init(getApplicationContext());

        isDone = false;

        if(getIntent().getExtras() != null){
            isDone = getIntent().getExtras().getBoolean("isDone");
        }

        timeRemaining = (TextView) findViewById(R.id.timeRemaingAlarm);

        TextView txtAviso = (TextView) findViewById(R.id.textAviso);

        TextView txtAvisoMenor = (TextView) findViewById(R.id.textAvisoMenor);

        sharedPrefs = getSharedPreferences(TimeOption.PREFERENCE_NAME, Context.MODE_PRIVATE);


        Button btnOk = (Button) findViewById(R.id.btnAlarmOk);

        Button btnRedefinir = (Button) findViewById(R.id.btnAlarmRedefinir);



        if(isDone){

            txtAviso.setText("O tempo acabou");

            timeRemaining.setText(sharedPrefs.getString(TimeOption.TIME_CHOSEN,"00:00:00"));

            txtAvisoMenor.setText("Completados");

            btnOk.setText("Finalizar");
        }else{
            ViewGroup layout = (ViewGroup) btnRedefinir.getParent();
            if(null!=layout) //for safety only  as you are doing onClick
                layout.removeView(btnRedefinir);
        }

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent("EXECUTE_ALARM");
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDone){
                    finalizeAlarm();
                }else{
                    android.os.Process.killProcess(android.os.Process.myPid());
                }


            }
        });

        btnRedefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itPanel = new Intent(getApplicationContext(), PainelActivity.class);
                startActivity(itPanel);

                finish();
            }
        });

        sharedPrefsNotification = getDefaultSharedPreferences(getApplicationContext());
        String strRingtonePreference = sharedPrefsNotification.getString("prefSound",null);


        if(strRingtonePreference == null) {
            ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(),
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        }else{
            ringtoneAlarm = RingtoneManager.getRingtone(getApplicationContext(),Uri.parse(strRingtonePreference));
        }

        initCountDown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initCountDown(){

        LocalTime timeCurr = new LocalTime();

        LocalTime timeChosen;

        if(sharedPrefs.getString(TimeOption.TIME_STARTED,null) == null) {
            timeChosen = new LocalTime(sharedPrefs.getString(TimeOption.TIME_CHOSEN,"00:00:00"));
        }else{

            String valor = sharedPrefs.getString(TimeOption.TIME_STARTED,"00:00:00");

            int valueDiff = new LocalTime().getMillisOfDay() - new LocalTime(sharedPrefs.getString(TimeOption.TIME_STARTED,"00:00:00")).getMillisOfDay();

            int valueChosenInMilis = new LocalTime(sharedPrefs.getString(TimeOption.TIME_CHOSEN,"00:00:00")).getMillisOfDay();

            if(valueChosenInMilis > valueDiff) {
                valueDiff = valueChosenInMilis - valueDiff;
            }else{
                valueDiff = 0;
            }

            timeChosen = new LocalTime().withMillisOfDay(valueDiff);
        }

        int period = (timeCurr.getMillisOfDay() + timeChosen.getMillisOfDay());

        period -= timeCurr.getMillisOfDay();

        if(sharedPrefs.getString(TimeOption.TIME_STARTED,null) == null) {
            SharedPreferences.Editor editor = sharedPrefs.edit();

            editor.putString(TimeOption.TIME_STARTED, DateTimeFormat.forPattern("HH:mm:ss").print(timeCurr));

            editor.commit();
        }

        startCountDown(period);

    }

    public void startCountDown(int period){

        countDown = new CountDownTimer(period, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            DateTimeFormatter mdate = DateTimeFormat.forPattern("HH:mm:ss");


            public void onTick(long millisUntilFinished) {

                timeRemaining.setText(mdate.print(new LocalTime().withMillisOfDay((int) millisUntilFinished)));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                //finalizeAlarm();
            }


        }
                .start();

    }

    @Override
    protected void onStart() {

        buildNotificationCommon();

        super.onStart();

        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        if(ringtoneAlarm.isPlaying())
        ringtoneAlarm.stop();

        if(v.hasVibrator()){
            v.cancel();
        }

    }

    private void buildNotificationCommon() {
        ringtoneAlarm.play();

        if(sharedPrefsNotification.getBoolean("prefVibrar", true)) {
            v.vibrate(new long[]{1000, 1000, 1000, 1000, 1000}, 1);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        /******block is needed to raise the application if the lock is*********/
        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    /* ^^^^^^^block is needed to raise the application if the lock is*/
    }

    public void finalizeAlarm(){

        SharedPreferences.Editor editor = sharedPrefs.edit();

        if(sharedPrefs.contains(TimeOption.TIME_CHOSEN))
            editor.remove(TimeOption.TIME_CHOSEN);

        if(sharedPrefs.contains(TimeOption.TIME_STARTED))
            editor.remove(TimeOption.TIME_STARTED);

        editor.commit();

        Toast.makeText(getApplicationContext(), "O seu tempo terminou", Toast.LENGTH_LONG).show();

        android.os.Process.killProcess(android.os.Process.myPid());

    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }
}
