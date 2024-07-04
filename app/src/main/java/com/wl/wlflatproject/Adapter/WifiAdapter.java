package com.wl.wlflatproject.Adapter;

import android.net.wifi.ScanResult;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wl.wlflatproject.Activity.SystemNetActivity;
import com.wl.wlflatproject.R;

import java.util.List;

public class WifiAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {
    public WifiAdapter(@Nullable List<ScanResult> data) {
        super(R.layout.item_wifi_list, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ScanResult accessPoint) {
        holder.setText(R.id.wifi_name_item, accessPoint.SSID)
                .setImageResource(R.id.wifi_state_item_iv, SystemNetActivity.isSecured(accessPoint) ? R.mipmap.ic_wifi_lock : R.mipmap.ic_wifi);
//        holder.setImageResource(R.id.iv_item_wifi_lable, srcs[accessPoint.getSignalLevel()]);
    }

}
