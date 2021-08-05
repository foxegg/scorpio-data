package com.newstar.scorpiodata.utils;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.newstar.scorpiodata.netutils.NetUtils;
import com.newstar.scorpiodata.risk.RiskType;

import java.util.ArrayList;
import java.util.List;

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

        PackageManager pm = activity.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(activity.getPackageName(), 0);
            //得到自己的包名
            String pkgName = pi.packageName;
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
            List<String> peimissions = new ArrayList<>();
            for(String allP:PermissionUtils.requestPermissions){
                for(String localP:pkgInfo.requestedPermissions){
                    if(allP.equals(localP)){
                        peimissions.add(allP);
                    }
                }
            }
            PermissionUtils.requestPermissions = new String[peimissions.size()];
            peimissions.toArray(PermissionUtils.requestPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
