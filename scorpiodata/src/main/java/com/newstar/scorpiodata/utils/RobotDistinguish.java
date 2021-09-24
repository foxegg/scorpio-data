package com.newstar.scorpiodata.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Date;

public class RobotDistinguish {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    // 将纳秒转化为秒
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angle[] = new float[3];

    private float defaultAccelerometer = 0;

    private boolean accelerometerNotChanged = true;

    private static int TIME_STEP = 2000;

    private boolean isRobot = false;

    public boolean isRobot() {
        return isRobot;
    }

    private static RobotDistinguish robotDistinguish;
    SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // x,y,z分别存储坐标轴x,y,z上的加速度
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                if (defaultAccelerometer == 0) {
                    defaultAccelerometer = Math.abs(x) + Math.abs(y) + Math.abs(z);
                } else {
                    accelerometerNotChanged = (defaultAccelerometer == (Math.abs(x) + Math.abs(y) + Math.abs(z)));
                }
                //Log.d("luolaigang", "x---------->" + x + "y-------------->" + y + "z----------->" + z);
                //Log.d("luolaigang", "---------->" + isRobot);
            }
            //Log.d("luolaigang", "lightNotChanged---------->" + lightNotChanged+" accelerometerNotChanged---------->" + accelerometerNotChanged);
            isRobot = accelerometerNotChanged;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public static RobotDistinguish getInstence(){
        if(robotDistinguish==null){
            robotDistinguish = new RobotDistinguish();
        }
        return robotDistinguish;
    }

    public void init(){
        sensorManager = (SensorManager) PluginInit.ACTIVITY.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometerSensor!=null){
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        defaultAccelerometer = 0;
    }

    public void onPause(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}
