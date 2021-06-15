package com.newstar.scorpiodata.risk;

import android.content.Context;

public class RiskType {
    public static final String PREFIX = "";
    public static final String CONTACTS = PREFIX + "contacts";
    public static final String IMAGE_LIST = PREFIX + "imageInfo";
    public static final String LOCATION = PREFIX + "location";
    public static final String SYS_OTHER_INFO = PREFIX + "otherRiskInfo";
    public static final String CELLINFO_LIST = PREFIX + "stationInfo";
    public static final String APP_LIST = PREFIX + "appNum";
    public static final String SMS_LIST = PREFIX + "smsInfo";

    public static Context context;
    public static String subChannel;
    public static void init(Context context, String subChannel){
        RiskType.context = context;
        RiskType.subChannel = subChannel;
    }
}
