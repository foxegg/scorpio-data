package com.newstar.scorpiodata.utils;

import android.util.Log;

import com.newstar.scorpiodata.BuildConfig;


public class LogUtils {
    public static void i(String value){
        if(value!=null){
            Log.i(BuildConfig.TAG, value);
        }
    }
}
