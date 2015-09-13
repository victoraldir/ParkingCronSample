package com.pw.victor.parkingcronsample.activities;

/**
 * Created by victor on 26/08/15.
 */
public class TimeOption {

    public static final String PREFERENCE_NAME = "PREF";

    public static final String TIME_CHOSEN = "TIME_CHOSEN";

    public static final String TIME_STARTED = "TIME_STARTED";


    public static String getCodeTimeChosen(String time){

        if(time.equals("00:15:00")){
            return "1/4 E";
        }else if(time.equals("00:30:00")){
            return "1/2 E";
        }else if(time.equals("01:00:00")){
            return "1 E";
        }else if(time.equals("02:00:00")){
            return "2 E";
        }else if(time.equals("03:00:00")){
            return "3 E";
        }else if(time.equals("04:00:00")){
            return "4 E";
        }else if(time.equals("05:00:00")){
            return "5 E";
        }else if(time.equals("06:00:00")){
            return "6 E";
        }

        return "N/A";
    }

}
