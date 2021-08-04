package com.newstar.scorpiodata.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.newstar.scorpiodata.risk.RiskType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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


    private static final String FILE_DIR = "/.data";
    private static final String FILE_NAME = "nsdata";

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

    /**
     * 获取系统uid
     * @return
     */
    public static String getUid(){
        String uid = null;
        try {
            uid = readUid();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(uid == null){
            try {
                write2File(getUidReal());
            } catch (IOException e) {
                e.printStackTrace();
            }
            uid = getUidReal();
        }
        return uid;
    }

    public static String readUid() throws IOException {
        String fileDirStr = Environment.getExternalStorageDirectory().getAbsolutePath()+FILE_DIR;
        File file = new File(fileDirStr, FILE_NAME);
        StringBuffer stringBuffer = new StringBuffer();
        // 打开文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int len = fileInputStream.read(buffer);
        // 读取文件内容
        while (len > 0) {
            stringBuffer.append(new String(buffer, 0, len));
            // 继续将数据放到buffer中
            len = fileInputStream.read(buffer);
        }
        // 关闭输入流
        fileInputStream.close();
        return stringBuffer.toString();
    }

    private static void write2File(String data) throws IOException {
        String fileDirStr = Environment.getExternalStorageDirectory().getAbsolutePath()+FILE_DIR;
        File fileDir = new File(fileDirStr);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDirStr, FILE_NAME);
        if (!file.exists() || !file.isFile()) {
            file.createNewFile();
        }
        OutputStream ou = null;
        try {
            ou = new FileOutputStream(file);
            byte[] buffer = data.getBytes();
            ou.write(buffer);
            ou.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ou != null) {
                    ou.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}