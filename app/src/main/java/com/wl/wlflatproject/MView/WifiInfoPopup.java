package com.wl.wlflatproject.MView;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SizeUtils;
import com.lxj.xpopup.core.CenterPopupView;
import com.wl.wlflatproject.Bean.AccessPoint;
import com.wl.wlflatproject.R;

import java.util.List;

/**
 * 显示当前网络数据
 */
public class WifiInfoPopup extends CenterPopupView implements View.OnClickListener {

    private String infoString = "信号强度：%s\nIP地址：%s\n安全性：%s";

    private String mCapabilities;
    TextView wifiInfoTv;

    private WifiInfo mWifiInfo;
    private WifiRemoveClickListener removeListener;

    public WifiInfoPopup(@NonNull Context context, WifiInfo wifiInfo, String capabilities, WifiRemoveClickListener listener) {
        super(context);
        mWifiInfo = wifiInfo;
        mCapabilities = capabilities;
        this.removeListener = listener;
    }

    protected void onCreate() {
        super.onCreate();
        wifiInfoTv = findViewById(R.id.info_tv);
        findViewById(R.id.remove_tv).setOnClickListener(this);
        if (mWifiInfo != null && !TextUtils.isEmpty(mWifiInfo.getSSID())) {

            int rssi = Math.abs(mWifiInfo.getRssi());
            int lv = AccessPoint.calculateSignalLevel(rssi, 3);
            //强度
            String signal = lv == 0 ? "弱" : lv == 1 ? "中等" : "强";
            String ipString = intToIp(mWifiInfo.getIpAddress());
            wifiInfoTv.setText(String.format(infoString, signal, ipString, mCapabilities));
        }

    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.wifi_info_popup_layout;
    }

    @Override
    public void onClick(View v) {
        if (removeListener != null) {
            removeListener.onRemoveClick(mWifiInfo.getSSID());
        }
        dismiss();
    }

    /**
     * wifi获取到的ip地址转换
     *
     * @param i
     * @return
     */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public interface WifiRemoveClickListener {
        void onRemoveClick(String ssid);
    }

    @Override
    protected int getMaxWidth() {
        return SizeUtils.dp2px(400);
    }
}
