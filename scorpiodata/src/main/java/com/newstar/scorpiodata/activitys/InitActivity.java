package com.newstar.scorpiodata.activitys;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.newstar.scorpiodata.utils.ViewHelp;

public class InitActivity extends AppCompatActivity {
    public boolean lightBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        ViewHelp.setFullscreen(this, lightBar);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    public void setLightBar(boolean lightBar) {
        this.lightBar = lightBar;
    }
}