package com.newstar.scorpiodata;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kochava.base.Tracker;
import com.newstar.scorpiodata.utils.LogUtils;
import com.newstar.scorpiodata.utils.SharedHelp;
import com.zing.zalo.zalosdk.oauth.ZaloSDKApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;
import ai.advance.liveness.lib.Market;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseCrashlytics mFirebaseCrashlytics = FirebaseCrashlytics.getInstance();
        mFirebaseCrashlytics.sendUnsentReports();
        FacebookSdk.setAutoInitEnabled(true);
        AppEventsLogger.activateApp(this);
        ZaloSDKApplication.wrap(this);
        initAdid();
        GuardianLivenessDetectionSDK.init(this, BuildConfig.livenessAccessKey, BuildConfig.livenessSecretKey, Market.Vietnam);
        GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();
    }

    private void initKochava(String kochavaGuid){
        // Start the Kochava Tracker
        Tracker.Configuration configuration = new Tracker.Configuration(this);
        configuration.setAppGuid(kochavaGuid);
        Tracker.configure(configuration.setAttributionUpdateListener(attribution -> {
                    // got the attribution results, now we need to parse it
                    try {
                        JSONObject attributionObject = new JSONObject(attribution);
                        SharedHelp.setSharedPreferencesValue(SharedHelp.KOCHAVE_REFERRER_URL, attribution);
                        if("false".equals(attributionObject.optString("attribution", ""))) {
                            // Install is not attributed.
                        } else {
                            // Install is attributed. Retrieve the values we care about.
                            final String attributedNetworkId = attributionObject.optString("network_id");
                            // ...
                            //Kochave kochave = new Gson().fromJson(attribution, Kochave.class);
                            //Log.i("luolaigangTracker",kochave.getCampaign());
                        }
                    } catch (JSONException exception) {
                    }
                })
        );
        Tracker.configure(configuration);
    }

    static int LENGTH = 4000;
    public static void i(String tag, String msg) {
        if (msg.length() > LENGTH) {
            for (int i = 0; i < msg.length(); i += LENGTH) {
                if (i + LENGTH < msg.length()) {
                    android.util.Log.i(tag, msg.substring(i, i + LENGTH));
                } else {
                    android.util.Log.i(tag, msg.substring(i, msg.length()));
                }
            }
        } else {
            android.util.Log.i(tag, msg);
        }
    }

    private void initAdid() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    String adid = info.getId();
                    LogUtils.i( adid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
