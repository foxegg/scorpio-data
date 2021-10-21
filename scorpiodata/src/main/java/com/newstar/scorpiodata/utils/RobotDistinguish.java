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
    private Sensor lightSensor;

    private boolean isRobot = true;

    public boolean isRobot() {
        return isRobot;
    }

    private static RobotDistinguish robotDistinguish;
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                isRobot = event.values[0]==0;
            }
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
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(lightSensor!=null){
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}