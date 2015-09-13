package com.pw.victor.parkingcronsample.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextClock;

import com.pw.victor.parkingcronsample.R;
import com.pw.victor.parkingcronsample.fragments.ButtonsFragment;
import com.pw.victor.parkingcronsample.fragments.PainelActiveFragment;


public class PainelActivity extends ActionBarActivity {

    private SharedPreferences sharedPrefs;

    private Context mContext;

    private FragmentManager fm;

    private Window wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);

        TextClock txtClk = (TextClock) findViewById(R.id.textClock);

        mContext = this;

        sharedPrefs = getSharedPreferences(TimeOption.PREFERENCE_NAME, Context.MODE_PRIVATE);


        fm = getFragmentManager();

        initFragment();

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

    private void initFragment(){

        FragmentTransaction ft = fm.beginTransaction();

        if(sharedPrefs.getString(TimeOption.TIME_CHOSEN,null) == null){

            ButtonsFragment btnFt = new ButtonsFragment();

            ft.replace(R.id.dynFrm, btnFt);

        }else{

            PainelActiveFragment paFt = new PainelActiveFragment();

            ft.replace(R.id.dynFrm, paFt);

        }

        ft.commit();
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
            Intent iConf = new Intent(this, UserSettingActivity.class);
            startActivityForResult(iConf, 1);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
