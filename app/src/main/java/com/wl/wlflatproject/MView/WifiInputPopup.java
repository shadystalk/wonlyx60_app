package com.wl.wlflatproject.MView;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.wl.wlflatproject.R;

/**
 * WiFi密码输入弹窗
 */
public class WifiInputPopup extends BasePopupView implements View.OnClickListener {
    private EditText mPwdEt;
    private TextView mWifiNameTv, mConnectionTv;
    private final StringInputListener mListener;
    private final ScanResult mScanResult;

    public WifiInputPopup(@NonNull Context context, ScanResult scanResult, StringInputListener listener) {
        super(context);
        mScanResult = scanResult;
        mListener = listener;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.cancel).setOnClickListener(this);
        mConnectionTv = findViewById(R.id.connection);
        mConnectionTv.setOnClickListener(this);
        mPwdEt = findViewById(R.id.pwd_et);
        mWifiNameTv = findViewById(R.id.wifi_name);
        mWifiNameTv.setText(mScanResult.SSID);
        mPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mConnectionTv.setEnabled(s.length() >= 8);
            }
        });
    }

    @Override
    protected int getInnerLayoutId() {
        return R.layout.wifi_input_popup;
    }


    @Override
    protected int getMaxWidth() {
        return SizeUtils.dp2px(600);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.connection:
                if (mListener != null) {
                    mListener.onInput(mScanResult, mPwdEt.getText().toString());
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface StringInputListener {
        void onInput(ScanResult mScanResult, String inputString);
    }
}
