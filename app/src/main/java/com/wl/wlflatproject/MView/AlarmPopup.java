package com.wl.wlflatproject.MView;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SizeUtils;
import com.lxj.xpopup.core.CenterPopupView;
import com.wl.wlflatproject.Bean.AccessPoint;
import com.wl.wlflatproject.R;

/**
 * 告警类弹窗
 */
public class AlarmPopup extends CenterPopupView implements View.OnClickListener {

    private String mTitle = "";

    TextView titleTv;
    TextView removeTv;
    String mConfirmation = "";
    String mNegationText = "";


    public AlarmPopup(@NonNull Context context, String titleText, String confirmation, String negationText) {
        super(context);
        mTitle = titleText;
        mConfirmation = confirmation;
        mNegationText = negationText;
    }

    public AlarmPopup(@NonNull Context context, String titleText, String confirmation) {
        super(context);
        mTitle = titleText;
        mConfirmation = confirmation;
    }

    protected void onCreate() {
        super.onCreate();
        titleTv = findViewById(R.id.info_tv);
        titleTv.setGravity(Gravity.CENTER);
        removeTv = findViewById(R.id.remove_tv);
        removeTv.setText(mConfirmation);
        removeTv.setOnClickListener(this);

        titleTv.setText(mTitle);

    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.wifi_info_popup_layout;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    protected int getMaxWidth() {
        return SizeUtils.dp2px(400);
    }
}
