package com.pw.victor.parkingcronsample.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pw.victor.parkingcronsample.activities.AlarmActivity;

/**
 * Created by victor on 27/08/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(context, AlarmActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle extras = intent.getExtras();
        if(extras.getBoolean("isDone")){
            it.putExtras(extras);
        }

        context.startActivity(it);
    }

}