package com.wl.wlflatproject.MUtils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;

import static android.content.Context.KEYGUARD_SERVICE;

public class DeviceUtils {
    //获取序列号
    public static String getSerialNumber(Context context){

        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
