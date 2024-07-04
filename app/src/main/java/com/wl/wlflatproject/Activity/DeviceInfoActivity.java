package com.wl.wlflatproject.Activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wl.wlflatproject.Constant.Constant;
import com.wl.wlflatproject.MUtils.SPUtil;
import com.wl.wlflatproject.MUtils.VersionUtils;
import com.wl.wlflatproject.R;

public class DeviceInfoActivity extends AppCompatActivity {
    /**
     * 产品型号，MAC地址，软件版本，前板版本，后板版本
     */
    private TextView mModelTv, mMacTv, mSoftwareTv, mPreviousTv,mAfterTv,machineTv;
    private View backIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_info_fragment);
        mModelTv = findViewById(R.id.model_text);
        mMacTv = findViewById(R.id.mac_text);
        mSoftwareTv =findViewById(R.id.software_version_text);
        mPreviousTv = findViewById(R.id.previous_version_text);
        mAfterTv = findViewById(R.id.after_version_text);
        backIv = findViewById(R.id.back_iv);
        machineTv = findViewById(R.id.machine_version_text);
        initData();
    }
    public void initData(){
        SPUtil spUtil = SPUtil.getInstance(this);
        String devId =spUtil.getSettingParam(Constant.DEVID, "");
        String fVer =spUtil.getSettingParam(Constant.FVER, "");
        String bVer =spUtil.getSettingParam(Constant.BVER, "");
        String machine =spUtil.getSettingParam(Constant.MACHINE, "");
        String devType = SPUtil.getInstance(this).getSettingParam(Constant.DEVTYPE, "");
        mModelTv.setText(devType);
        mPreviousTv.setText(fVer);
        mAfterTv.setText(bVer);
        mMacTv.setText(devId);
        machineTv.setText(machine);
        mSoftwareTv.setText(VersionUtils.getVersionName(this));
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 16) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN //hide statusBar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //hide navigationBar
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }
}
