package com.newstar.scorpiodata.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.newstar.scorpiodata.risk.RiskType;

import java.util.UUID;

public class SharedHelp {
    public static String UID = "uid";
    public static String SHOW_PERMISSINOS = "show_permissinos";
    public static String UPDATE_UID = "update_uid";
    public static final String USER_GID = "userGid";
    public static final String TOKEN = "token";
    public static final String GOOGLE_REFERRER_URL = "referrerUrl";
    public static final String KOCHAVE_REFERRER_URL = "kochaveReferrerUrl";
    public static final String FIRST_LOADED = "first_loaded";
    public static final String IS_FIRST_INSTALL = "is_first_install";
    public static final String H5_HOST = "h5_host";

    public final static String UPDATE_VERSION_PREFIX_KEY = "update_version_";
    /**
     * 获取系统uid
     * @return
     */
    public static String getUid(){
        return getUidReal();
    }

    /**
     * 获取手机设备唯一码
     * @return
     */
    public static String getUidReal()  {
        final String buildInfo, androidId;
        StringBuffer buildSB = new StringBuffer();
        buildSB.append(Build.BRAND).append("/");
        buildSB.append(Build.PRODUCT).append("/");
        buildSB.append(Build.DEVICE).append("/");
        buildSB.append(Build.ID).append("/");
        buildSB.append(Build.VERSION.INCREMENTAL);
        buildInfo = buildSB.toString();
        androidId = "" + android.provider.Settings.Secure.getString(PluginInit.ACTIVITY.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), buildInfo.hashCode());
        return PluginInit.SUB_CHANNEL+"-"+deviceUuid.toString();
    }

    public static String getSharedPreferencesValue(String key) {
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences = PluginInit.ACTIVITY.getSharedPreferences("data", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void setSharedPreferencesValue(String key, String value) {
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences = PluginInit.ACTIVITY.getSharedPreferences("data", Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        editor.putString(key, value);
        //步骤4：提交
        editor.apply();
    }
}