package com.newstar.scorpiodata.utils;

import android.app.Activity;
import android.app.Application;

import com.android.volley.toolbox.Volley;
import com.newstar.scorpiodata.netutils.NetUtils;
import com.newstar.scorpiodata.risk.RiskType;

public class PluginInit {
    public static Activity ACTIVITY;
    public static Application APPLICATION;
    public static String CHANNEL;
    public static String SUB_CHANNEL;
    public static String HOST;
    public static String PRODUCT;
    public static void init(Activity activity, Application application, String channel, String subChannel,String host,String product){
        ACTIVITY = activity;
        APPLICATION = application;
        CHANNEL = channel;
        SUB_CHANNEL = subChannel;
        HOST = host;
        PRODUCT = product;
        NetUtils.init();
    }
}
