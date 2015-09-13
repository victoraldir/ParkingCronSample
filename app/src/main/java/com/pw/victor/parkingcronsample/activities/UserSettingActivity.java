package com.pw.victor.parkingcronsample.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.pw.victor.parkingcronsample.R;

/**
 * Created by victor on 05/09/15.
 */
public class UserSettingActivity extends PreferenceActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

    }
}
